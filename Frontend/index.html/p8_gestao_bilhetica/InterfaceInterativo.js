class InterfaceMapaInterativo {
    constructor() {
        this.nome = "InterfaceMapaInterativo";
        this.endpointMapa = "http://localhost:8081/api/gis/render/dados-mapa";
    }

    async obterDadosMapa() {
        const response = await fetch(this.endpointMapa);
        return await response.json();
    }

    obterNome() {
        return this.nome;
    }
}