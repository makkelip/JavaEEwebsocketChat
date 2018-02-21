
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
    printAllMessages(message);
  }
}

function printMessage(message) {
  let msgElement = document.createElement("div");
  let text = document.createTextNode(message.name + ": " + message.message);
  msgElement.appendChild(text);
  chatWindow.appendChild(msgElement);
}

function printAllMessages(message) {

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
  let whoOnline = {
    action: "waat"
  }
  socket.send(JSON.stringify(whoOnline));
}

function sendMessage() {
  let messageText = document.getElementById("message").value;
  let message = {
    action: "message",
    message: messageText
  };
  socket.send(JSON.stringify(message));
}
