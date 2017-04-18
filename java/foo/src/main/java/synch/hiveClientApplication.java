package synch;


import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import synch.resources.HelloWorldResource;
import synch.resources.HiveResource;
import synch.health.TemplateHealthCheck;



public class hiveClientApplication extends Application<hiveClientConfiguration> {

    public static void main(final String[] args) throws Exception {
        new hiveClientApplication().run(args);
    }

    @Override
    public String getName() {
        return "hiveClient";
    }

    @Override
    public void initialize(final Bootstrap<hiveClientConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final hiveClientConfiguration configuration,
                    final Environment environment) {
         final HelloWorldResource resource = new HelloWorldResource(
        configuration.getTemplate(),
        configuration.getDefaultName()
    );
    final HiveResource resource2=new HiveResource();
    environment.jersey().register(resource2);

    final TemplateHealthCheck healthCheck = new TemplateHealthCheck(configuration.getTemplate());
    environment.healthChecks().register("template", healthCheck);
    environment.jersey().register(resource);
        // TODO: implement application
    }

}
