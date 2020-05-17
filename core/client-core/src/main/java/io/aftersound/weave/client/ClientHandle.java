package io.aftersound.weave.client;

class ClientHandle<CLIENT> {

    private final CLIENT client;
    private final Endpoint endpoint;

    private ClientHandle(CLIENT client, Endpoint endpoint) {
        this.client = client;
        this.endpoint = endpoint;
    }

    static <CLIENT> ClientHandle of(CLIENT client, Endpoint endpoint) {
        return new ClientHandle(client, endpoint);
    }

    int optionsHash() {
        return endpoint.getOptions().hashCode();
    }

    CLIENT client() {
        return client;
    }

    Endpoint endpoint() {
        return endpoint;
    }
}
