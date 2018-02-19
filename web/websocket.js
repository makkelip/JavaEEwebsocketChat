window.onLoad = init;
let socket = new WebSocket("ws://192.168.0.105:8080/TestingUserAndRealtime/actions");
socket.onmessage = onMessage;
const username;
const chatWindow;

document.addEventListener("DOMContentLoaded", function() {
  chatWindow = document.getElementById("chat");
});

function onMessage(event){
  let message = JSON.parse(event.data);
  if (message.action === "newMessage") {
    printMessage(message);
  } else if (message.action === "loginFailed") {
    loginFailed(message);
  } else if (message.action === "loginSuccess") {
    loginSuccess();
  } else if (message.action === "allMessages") {
    printAllMessages(message);
  }
}

function printMessage(message) {
  
}

function printAllMessages(message) {

}
function login(loginName) {
  let loginMessage = {
    action: "login",
    name: loginName
  };
  socket.send(JSON.stringify(loginMessage));
}

function loginFailed(message) {
  alert(message.error);
}

function loginSuccess() {

}

function sendMessage(messageText) {
  let message = {
    action: ,
    message: messageText
  };
  socket.send(JSON.stringify(message));
}
