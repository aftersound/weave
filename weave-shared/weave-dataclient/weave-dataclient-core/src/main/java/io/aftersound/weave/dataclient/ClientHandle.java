package io.aftersound.weave.dataclient;

class ClientHandle<CLIENT> {

    private final CLIENT client;

    private ClientHandle(CLIENT client) {
        this.client = client;
    }

    static <CLIENT> ClientHandle of(CLIENT client) {
        return new ClientHandle(client);
    }

    CLIENT client() {
        return client;
    }
}
