class InterfaceConfiguracaoAnalise {
    constructor() {
        this.nome = "InterfaceConfiguracaoAnalise";
        this.endpointCalculo = "http://localhost:8081/analise/calcular";
    }

    async calcular(linhaId, periodo) {
        const payload = {
            linhaId: linhaId,
            periodo: periodo
        };

        const response = await fetch(this.endpointCalculo, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        return await response.json();
    }

    obterNome() {
        return this.nome;
    }
}