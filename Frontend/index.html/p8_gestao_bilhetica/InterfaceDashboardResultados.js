class InterfaceDashboardResultados {
    constructor() {
        this.nome = "InterfaceDashboardResultados";
        this.endpointResultados = "http://localhost:8081/api/bilhetica/dashboard/resultados";
    }

    async obterResultados(linhaId, periodo) {
        const params = new URLSearchParams();

        if (linhaId !== undefined && linhaId !== null && linhaId !== "") {
            params.append("linhaId", linhaId);
        }

        if (periodo !== undefined && periodo !== null && periodo !== "") {
            params.append("periodo", periodo);
        }

        const url = `${this.endpointResultados}?${params.toString()}`;
        const response = await fetch(url);
        return await response.json();
    }

    obterNome() {
        return this.nome;
    }
}