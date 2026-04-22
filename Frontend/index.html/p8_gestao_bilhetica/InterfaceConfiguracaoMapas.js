class InterfaceConfiguracaoMapas {
    constructor() {
        this.nome = "InterfaceConfiguracaoMapas";
        this.endpointConfiguracao = "http://localhost:8081/api/bilhetica/mapas/configuracao";
    }

    async obterConfiguracao() {
        const response = await fetch(this.endpointConfiguracao);
        return await response.json();
    }

    async atualizarConfiguracao(configuracao) {
        const response = await fetch(this.endpointConfiguracao, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(configuracao)
        });

        return await response.json();
    }

    obterNome() {
        return this.nome;
    }
}