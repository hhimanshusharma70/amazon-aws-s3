package com.crater.ammazonawstest.application;

import io.dropwizard.Application;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.crater.ammazonawstest.resource.ProductResource;
import com.crater.ammazonawstest.resource.impl.ProductResourceImpl;
import com.google.common.collect.Lists;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 
 * @author Navrattan Yadav
 *
 */
public class CoreApplication extends Application<CoreServiceConfiguration> implements Managed{

	private ClassPathXmlApplicationContext classPathXmlApplicationContext;

	public static void main(final String[] args) throws Exception {
		new CoreApplication().run(Lists.newArrayList("server", "application.yaml").
				toArray(new String[2]));
	}

	@Override
	public void run(final CoreServiceConfiguration configuration, 
			final Environment environment) throws Exception {
		classPathXmlApplicationContext = new ClassPathXmlApplicationContext(
				"spring/application-config.xml");
		
		
		final ProductResource productResource = classPathXmlApplicationContext.
				getBean(ProductResourceImpl.class);
		
		environment.jersey().register(productResource);
		environment.lifecycle().manage(this);
	}

	@Override
	public String getName() {
		return "wedoshoes-application";
	}

	@Override
	public void initialize(final Bootstrap<CoreServiceConfiguration> bootstrap) {
		// nothing to do yet
	}

	@Override
	public void start() throws Exception {
		// Do nothing
	}

	@Override
	public void stop() throws Exception {
		final ComboPooledDataSource comboPooledDataSource = classPathXmlApplicationContext
				.getBean(ComboPooledDataSource.class);
		comboPooledDataSource.close();
		classPathXmlApplicationContext.close();
	}
}