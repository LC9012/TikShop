let currentChatId = null;
function formatDateTime(dtstr) {
    let d = new Date(dtstr);
    return d.toLocaleString("it-IT", { dateStyle: "short", timeStyle: "short" });
}
function loadChats() {
    fetch('SupportServlet?action=listChats')
      .then(r=>r.json())
      .then(d=>{
        let el = document.getElementById('chatList');
        el.innerHTML = "";
        if (!d.chats || d.chats.length==0) {el.innerHTML = "<div class='text-secondary'>Nessuna chat precedente.</div>"; return;}
        d.chats.forEach(chat=>{
            let div = document.createElement('div');
            div.className = "chat-item";
            div.innerHTML = "<span>Chat #" + chat.id + "</span><span class='chat-date'>" + formatDateTime(chat.created_at) + "</span>";
            div.onclick = ()=>openChat(chat.id);
            el.appendChild(div);
        });
      });
}
function openChat(chatId) {
    currentChatId = chatId;
    document.getElementById('chatModalBg').classList.add('show');
    loadMessages(chatId);
}
function loadMessages(chatId) {
    fetch('SupportServlet?action=getMessages&chatId=' + chatId)
      .then(r=>r.json())
      .then(d=>{
        let messages = d.messages || [];
        let el = document.getElementById('chatMessages');
        el.innerHTML = "";
        messages.forEach(m=>{
            let bubble = document.createElement('div');
            bubble.className = "chat-bubble " + (m.sender=="user" ? "bubble-user" : "bubble-support");
            bubble.textContent = m.message;
            let time = document.createElement('span');
            time.className = "chat-time";
            time.textContent = formatDateTime(m.sent_at);
            bubble.appendChild(time);
            el.appendChild(bubble);
        });
        el.scrollTop = el.scrollHeight;
    });
}
document.getElementById('chatForm').addEventListener('submit', function(e){
    e.preventDefault();
    let input = document.getElementById('chatInput');
    let msg = input.value.trim();
    if (!msg) return;
    // Mostra messaggio inviato
    let el = document.getElementById('chatMessages');
    let bubble = document.createElement('div');
    bubble.className = "chat-bubble bubble-user";
    bubble.textContent = msg;
    el.appendChild(bubble);
    el.scrollTop = el.scrollHeight;
    input.value = "";

    // Salva messaggio utente
    fetch('SupportServlet', {
        method:'POST',
        headers:{'Content-Type':'application/x-www-form-urlencoded'},
        body:'action=sendMessage&chatId='+currentChatId+'&message='+encodeURIComponent(msg)
    }).then(() => {
        // Mostra dots typing per 5 secondi
        let dots = document.createElement('div');
        dots.className = "chat-bubble bubble-support";
        let typing = document.createElement('div');
        typing.className = "typing-dots";
        typing.innerHTML = '<div class="typing-dot"></div><div class="typing-dot"></div><div class="typing-dot"></div>';
        dots.appendChild(typing);
        el.appendChild(dots);
        el.scrollTop = el.scrollHeight;

        setTimeout(()=>{
          // Rimuovi dots, mostra risposta automatica
          dots.remove();
          let reply = document.createElement('div');
          reply.className = "chat-bubble bubble-support";
          reply.textContent = "Segnalazione ricevuta, ti risponderemo al più presto";
          let time = document.createElement('span');
          time.className = "chat-time";
          time.textContent = formatDateTime(new Date());
          reply.appendChild(time);
          el.appendChild(reply);
          el.scrollTop = el.scrollHeight;
          // Salva la risposta nel DB
          fetch('SupportServlet', {
              method:'POST',
              headers:{'Content-Type':'application/x-www-form-urlencoded'},
              body:'action=replyAuto&chatId='+currentChatId
          });
        }, 5000);
    });
});
document.getElementById('closeChatBtn').onclick = function(){
    document.getElementById('chatModalBg').classList.remove('show');
    currentChatId = null;
};
document.getElementById('newChatBtn').onclick = function(){
    fetch('SupportServlet', {
        method:'POST',
        headers:{'Content-Type':'application/x-www-form-urlencoded'},
        body:'action=newChat'
    }).then(r=>r.json())
      .then(d=>{
        openChat(d.chatId);
    });
};
window.onload = loadChats;