
let socket = new WebSocket("ws://localhost:8080/testChat/actions");
socket.onmessage = onMessage;
let username;
let messageWindow;
let loginWindow;
let chatWindow;

window.onload = function() {
  chatWindow = document.getElementById("chat");
  messageWindow = document.getElementById("typeMessage");
  chatWindow = document.getElementById("chat");
  document.getElementById("login").addEventListener("click", login);
  document.getElementById("send").addEventListener("click", sendMessage);
  messageWindow.style.display = 'none';
};

function onMessage(event){
  let message = JSON.parse(event.data);
  if (message.action === "message") {
    printMessage(message);
  } else if (message.action === "loginFailed") {
    console.log("login failed");
    loginFailed(message);
  } else if (message.action === "loginSuccess") {
    console.log("login success");
    loginSuccess(message);
  } else if (message.action === "whoOnlineResponse") {

  } else if (message.action === "getMessages") {
    console.log("got all messages");
    allMessages(message);
  }
}

function singleMessage(message) {
  printMessage(message.name, message.message);
}

function allMessages(message) {
  let allMessages = message.messages;
  Object.entries(allMessages).forEach(
    ([name, text]) => printMessage(name, text)
  );
}

function printMessage(name, text) {
  let msgElement = document.createElement("div");
  let text = document.createTextNode(name + ": " + text);
  msgElement.appendChild(text);
  chatWindow.appendChild(msgElement);
}

function login() {
  let loginName = document.getElementById("loginName").value;
  console.log(loginName);
  if (loginName === "") {
    alert("Requres name");
  } else {
    let loginMessage = {
      action: "login",
      name: loginName
    };
    socket.send(JSON.stringify(loginMessage));
  }
}

function loginFailed(message) {
  alert(message.note);
}

function loginSuccess(message) {
  alert(message.note);
  messageWindow.style.display = '';
  document.getElementById("loginWindow").style.display = 'none';
  let getAllMessages = {
    action: "getMessages"
  }
  socket.send(JSON.stringify(getAllMessages));
}

function sendMessage() {
  let messageText = document.getElementById("message").value;
  let message = {
    action: "message",
    message: messageText
  };
  socket.send(JSON.stringify(message));
}
