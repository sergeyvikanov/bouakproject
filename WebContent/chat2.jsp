<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Apache Tomcat WebSocket Examples: Chat</title>
    <link rel="stylesheet" href="style.css">
	<script src="jquery-2.1.3.min.js"></script>
    <script type="application/javascript">
        "use strict";
	
        var Chat = {};
        var toSend = {};
		var msg='';
		var p1;
		var dive;
		var bazar;
		var p2;
		var debut=false;
        Chat.socket = null;

        Chat.connect = (function(host) {
            if ('WebSocket' in window) {
                Chat.socket = new WebSocket(host);
            } else if ('MozWebSocket' in window) {
                Chat.socket = new MozWebSocket(host);
            } else {
                //Console.log('Error: WebSocket is not supported by this browser.');
                return;
            }

            Chat.socket.onopen = function () {
                //Console.log('Info: WebSocket connection opened.');
                document.getElementById('chat').onkeydown = function(event) {
                    if (event.keyCode == 13) {
                        Chat.sendMessage();
                    }
                };
            };

            Chat.socket.onclose = function () {
                document.getElementById('chat').onkeydown = null;
                //Console.log('Info: WebSocket closed.');
            };

            Chat.socket.onmessage = function (message) {
                var obj = JSON.parse(message.data);
                if (obj.sender == "BOT")
                {
                    if(obj.content == "fini")
                    {
                        dive.html("");
                        debut = false;
                        clearInterval(bazar);
                    }
                    else if(obj.type == "CON")
                    {
                    	$("#player2").html(obj.content);
                    	$("#player2").addClass("connected");
                    }
                    else
                    {    
                	msg = msg + obj.content;
    				debut=true;
                    }
                }
                else if (obj.sender == "Joueur0")
                {
                	$('#player1').append(obj.content);
                }
                else if (obj.sender == "Joueur1")
                {
                	$('#player2').append(obj.content);
                }
                else if (obj.sender == "BOTREP")
                {
                	$('#resultat').append(obj.content);
                }
                
                
            };
            
        });

        Chat.initialize = function() {
            if (window.location.protocol == 'http:') {
                Chat.connect('ws://' + window.location.host + '/bouakproject/websocket/chat/5');
            } else {
                Chat.connect('wss://' + window.location.host + '/bouakproject/websocket/chat/5');
            }
        };

        Chat.sendMessage = (function() {
            var message = document.getElementById('chat').value;
            if (message != '') {                
                toSend.sender = $('#player1').text();
                toSend.type = "REP";
                toSend.content = message;
                Chat.socket.send(JSON.stringify(toSend));
                document.getElementById('chat').value = '';
            }
        });

        var Console = {};

        Console.log = (function(message) {
            var console = document.getElementById('console');
            var p = document.createElement('p');
            msg = msg + message;
            p.style.wordWrap = 'normal';
            p.innerHTML = message.trim();
            console.appendChild(p);
            while (console.childNodes.length > 25) {
                console.removeChild(console.firstChild);
            }
            console.scrollTop = console.scrollHeight;
        });

        Chat.initialize();


        document.addEventListener("DOMContentLoaded", function() {
            // Remove elements with "noscript" class - <noscript> is not allowed in XHTML
            var noscripts = document.getElementsByClassName("noscript");
            for (var i = 0; i < noscripts.length; i++) {
                noscripts[i].parentNode.removeChild(noscripts[i]);
            }
        }, false);
		$( document ).ready(function()
		{	
					
			dive = $('#console');
			var a = 0;
			var aOld=0;
			var delay = 50;
			var now, before = new Date();
			//var chaine ="Joueur nÃ© en 1980, j'effectue toute ma formation Ã  l'ASSE, mais je n'y passe jamais pro, barrÃ© par des joueurs plus prÃ©coces. Je me retrouve Ã  jouer dans un club vÃ©gÃ©tatif typique du National (mais qui cette annÃ©e 2012 va monter en L1) dans lequel le club Nantais me repÃ¨re. A Nantes je vis vÃ©ritablement mes plus belles annÃ©es, puisque je remporte le titre de champion dÃ¨s ma premiÃ¨re saison. C'est logiquement que je rejoins un club ambitieux, juste dans la foulÃ©e de la finale de coupe perdue contre Sochaux. Joueur hargneux et plutÃ´t bourrin, j'ai du mal Ã  m'imposer dans ce club mÃ©diatisÃ©, mais je finis par gagner ma place (j'y joue encore), malgrÃ© une valse des coachs impressionnante. Je suis ??";

			bazar = setInterval(function() 
			{
				if (debut==true)
				{
					now = new Date();
					aOld = a;
					var elapsedTime = (now.getTime() - before.getTime());
					if(elapsedTime > delay+2)
						//Recover the motion lost while inactive.
						a += Math.round(elapsedTime/delay);
					else
						a++;				
					
					dive.append(msg.substring(aOld, a));
					before = new Date();
				}
				else
				{
					before = new Date();
				}
				
			}, delay);

			$("#target").click(function() {
				toSend.sender = $('#player1').text();
                toSend.type = "RDY";
                toSend.content = "1";
                Chat.socket.send(JSON.stringify(toSend));
                $("#target").prop('disabled', true);
				});
		});
		
        
        
	
    </script>
</head>
<body>
<div class="noscript"><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websockets rely on Javascript being enabled. Please enable
    Javascript and reload this page!</h2></div>
<div id = "conteneur">
    <p id = "champreponse">
        <input type="text" placeholder="tapez votre reponse ici" id="chat" />
    </p>
    <div id="player1" class="connected">${sessionScope.login}</div>
    <div id="console-container">
        <div id="console"></div>
    </div>
    <div id="player2">
        <p></p>
    </div>
    <div id="resultat">
    
    </div>
    <input type="button" id="target" value ="pret !"/>
</div>
</body>
</html>