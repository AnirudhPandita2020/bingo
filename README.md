# 🎮 Bingo Multiplayer

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F)
![WebSocket](https://img.shields.io/badge/WebSocket-Real--Time-blue)
![JUnit5](https://img.shields.io/badge/JUnit-5-red)
![Mockito](https://img.shields.io/badge/Mockito-Testing-green)

A scalable, event-driven multiplayer Bingo backend built using **Spring Boot** and **WebSockets**.

## ✨ Features

- Create and join multiplayer Bingo rooms
- Real-time gameplay using WebSockets
- Turn-based number calling
- Automatic Bingo board generation
- Server-side Bingo validation
- Event-driven architecture
- Domain-driven design
- Comprehensive unit testing

## 🏗 Architecture

```mermaid
flowchart TB
    Client["🎮 WebSocket Clients"]

    subgraph Transport["Transport Layer"]
        Handler["BingoWebSocketHandler"]
        Dispatcher["InboundMessageDispatcher"]
        Serializer["MessageSerializer"]
    end

    subgraph Application["Application Layer"]
        Service["RoomService"]
        Publisher["BingoEventPublisher"]
    end

    subgraph Domain["Domain Layer"]
        Room["Room"]
        Game["BingoGame"]
        Board["BingoBoard"]
        Player["Player"]
    end

    subgraph Messaging["Messaging Layer"]
        Mapper["OutboundMessageMapper"]
        Listeners["Event Listeners"]
    end

    subgraph Infrastructure["Infrastructure"]
        Repository["RoomRepository"]
        Registry["SessionRegistry"]
    end

    Client -->|Inbound JSON| Handler
    Handler --> Dispatcher
    Dispatcher --> Service
    Service --> Room
    Room --> Game
    Game --> Board
    Room --> Player
    Service --> Repository
    Service -->|Publishes| Publisher
    Publisher --> Listeners
    Listeners --> Mapper
    Mapper --> Serializer
    Serializer --> Registry
    Registry -->|Outbound JSON| Client
```

## 📡 Event Flow

```mermaid
sequenceDiagram
    participant Client
    participant WS as WebSocketHandler
    participant Dispatcher
    participant Service as RoomService
    participant Publisher
    participant Listener
    participant Mapper
    participant Registry
    Client ->> WS: CREATE_ROOM
    WS ->> Dispatcher: Dispatch Command
    Dispatcher ->> Service: createRoom()
    Service ->> Publisher: RoomCreatedEvent
    Publisher ->> Listener: onEvent()
    Listener ->> Mapper: map(event)
    Mapper -->> Listener: OutboundMessage
    Listener ->> Registry: fetchRoomSessions()
    Listener ->> Client: ROOM_CREATED
```

## 🚀 Running

```bash
mvn clean install
mvn spring-boot:run
```

## 🧪 Testing

- Domain tests
- RoomService tests
- SessionRegistry tests
- OutboundMessageMapper tests
- Event Listener tests
- WebSocket tests
