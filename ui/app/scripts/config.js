
    var CONFIG = {
        appUrl: document.location.protocol + "//" + document.location.host + "/api/",
        //appUrl: 'http://localhost:8081/',
        wsUrl: document.location.protocol == "https:" ? "wss:" : "ws:" + "//" + document.location.host + "/ws/",
        //wsUrl: 'ws://localhost:8082',
        //hawtioUrl: 'http://localhost:8080/'
        hawtioUrl: document.location.protocol + "//" + document.location.host + "/hawtio/"
    };
