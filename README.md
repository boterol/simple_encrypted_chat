# simple_encrypted_chat

## REQUERIMIENTOS TÉCNICOS: 
* Se requiere Java 11. 
* Se requiere gradle 6.6


## IMPORTANTE: 
El archivo build.gradle es distinto dependiendo el sistema operativo que corre la máquina. Por lo ranto, 

* Si Se usa WINDOWS, debe usar el proyecto en la rama main.

* Si se usa LINUX, debe usar el proyecto en la rama linux-version.  

## CONFIGURACIÓN DE CLIENTE y SERVIDOR:
dentro de las carpetas server y client están los archivos server.cfg y client.cfg respsectivamente. En estos archivos se definen las IPs de cliente y servidor de modo que puedan comunicarse 

* server.cfg:
    Service.Endpoints = default -h <<SERVER IP>> -p <<SERVER PORT>>

* client.cfg: 
    ChatServer.Proxy = SimpleChat: default -h <<SERVER IP>> -p <<SERVER PORT>>

    Chat.Client.Endpoints = default -h <<CLIENT IP>>