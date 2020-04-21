# ApplicationServer

### To run: 
```bash
--> Make sure you're running from ApplicationServer/build/classes/
Each in a separate terminal: 

# Run WebServer
$ java web.SimpleWebServer ../../config/WebServer.properties

# Run Satellite
$ java appserver.satellite.Satellite ../../config/Satellite.Earth.properties ../../config/WebServer.properties ../../config/Server.properties

# Run Client
$ java appserver.job.impl.PlusOneClient 
```

### Errors

If you get this error while trying to run SimpleWebServer: 
```bash
IOExceptionAddress already in use
java.net.BindException: Address already in use
...
```
Fix with: 
Kill process Terminal: kill <pid>

Find pid: Terminal: lsof -i:<port>

