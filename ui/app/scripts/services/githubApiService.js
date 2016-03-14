'use strict';
angular
    .module('apollo')
    .service('githubApiService', [
        '$q', '$http',
        ApiService
    ]);

function ApiService($q, $http){

    var getCommitDetails = function(githubProjectUrl, githubCommitSha) {

        var organization = githubProjectUrl.split('/')[3];
        var repository = githubProjectUrl.split('/')[4];

        return $http.get(CONFIG.githubApi + "/repos/" + organization + "/" + repository + "/commits/" + githubCommitSha, {
            headers: {'Authorization': 'token ' + CONFIG.githubToken}
        });
    };

    return {
      getCommitDetails: getCommitDetails
    };
}
