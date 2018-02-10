# Apollo

### What is Apollo?
A simple, lightweight, CD solution on top of Kubernetes.

Apollo can integrate with any building process you might have. You just need to notify it of a ready artifact, and that's it.
Apollo also gives a restricted access on top of Kubernetes.

Each user has fine-grained permissions, to only deploy what it need.

Please see the `Wiki` for the full docs

### Main Features
- Deploy a combination of a kubernetes Deployment and Service into designated kubernetes cluster and namespace. 
- View logs, revert deployments and get back to any point in time with just 1-click
- Manage multiple kubernetes clusters and multiple namespaces in the same cluster
- Full permissions model for deployments. Each user can deploy only what he needs
- Live query on kubernetes to get the current status of the deployments. You can also view pods status, view logs from each pod, and restart each pod
- Full restful API, and Java client to automate whatever you need, or deploy automatically
- Once initially deployed, you can deploy future versions of Apollo, from Apollo


### Run it
Apollo require a hocon format configuration file to get all of its resources. Configuration can be supplied as a Filesystem path or as a Consul Key.

Configuration example:
```hocon
apollo {
  db {  # Self explenatory
    port = 3306
    host = "..."
    user = "apollo"
    password = "..."
    schema = "apollo"
  }

  api {
    listen = "0.0.0.0"  # Where should apollo backend listen
    port = 8081  # And on which port
    secret = "SuperTestingSecret"  # Secret to encrypt websessions with
  }

  kubernetes {
    monitoringFrequencySeconds = 5  # How frequent should the apollo's kubernetes monitoring thread check the deployment statuses
  }

  scm {
    githubLogin = ""  # Github user (in case you need private repositories access)
    githubOauthToken = ""  # Access token created in your user settings. Should have view access for private repos
  }
}
```
Under `examples` you can find a simple Docker Compose to help you set it up localy

```bash
cd examples/
docker-compose up -d
```

### Contributing
Fork away, commit, and send a pull request.