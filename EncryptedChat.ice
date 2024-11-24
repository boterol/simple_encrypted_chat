module Demo {
    interface ChatClient {
        void receiveMessage(string msg);
    }

    interface ChatServer {
        string sendMessage(string msg);
        void registerClient(string hostname, ChatClient* proxy);
        void unregisterClient(string hostname);
        void SetProtocolValues(int value);
        string getProtocolValues();
        string getGN();
        void shutdown();
        int getClientCount();
    }
}
