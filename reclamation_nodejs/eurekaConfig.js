// eurekaConfig.js
const EurekaConfig = {
    instance: {
        instanceId: `reclamation:${process.env.PORT || "8500"}`, // Utilisation correcte du port
        app: "reclamation-service", // Nom plus clair pour Eureka
        hostName: "reclamation", // Nom du service dans Docker Compose
        ipAddr: "127.0.0.1",
        statusPageUrl: `http://reclamation:${process.env.PORT || "8500"}/reclamation/getAll`, // Utiliser le nom du service
        port: {
            $: process.env.PORT || "8500", // Correspond au port dans docker-compose.yml
            "@enabled": "true",
        },
        vipAddress: "reclamation-service", // Même que app
        dataCenterInfo: {
            "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
            name: "MyOwn",
        },
    },
    eureka: {
        host: "discovery-service", // Correct, correspond au nom du service Eureka
        port: 8761,
        servicePath: "/eureka/apps/",
        registerWithEureka: true,
        fetchRegistry: true,
    },
};

module.exports = EurekaConfig;