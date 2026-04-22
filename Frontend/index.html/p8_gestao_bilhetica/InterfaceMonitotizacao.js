class InterfaceMonitorizacao {
    constructor() {
        this.nome = "InterfaceMonitorizacao";
        this.endpointSincronizacao = "http://localhost:8081/api/bilhetica/sincronizar";
    }

    async executarSincronizacao() {
        const response = await fetch(this.endpointSincronizacao);
        return await response.json();
    }

    obterNome() {
        return this.nome;
    }
}