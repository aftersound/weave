package io.aftersound.weave.dataclient;

class ClientHandle<CLIENT> {

    private final CLIENT client;

    private volatile int optionsHash;

    private ClientHandle(CLIENT client) {
        this.client = client;

    }

    static <CLIENT> ClientHandle of(CLIENT client) {
        return new ClientHandle(client);
    }

    ClientHandle<CLIENT> bindOptionsHash(int optionsHash) {
        this.optionsHash = optionsHash;
        return this;
    }

    int optionsHash() {
        return optionsHash;
    }

    CLIENT client() {
        return client;
    }
}
