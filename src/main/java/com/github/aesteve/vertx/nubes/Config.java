package com.github.aesteve.vertx.nubes;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.RoutingContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.github.aesteve.vertx.nubes.auth.AuthMethod;
import com.github.aesteve.vertx.nubes.context.RateLimit;
import com.github.aesteve.vertx.nubes.exceptions.MissingConfigurationException;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessorRegistry;
import com.github.aesteve.vertx.nubes.handlers.Processor;
import com.github.aesteve.vertx.nubes.reflections.RouteRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.TypedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.services.ServiceRegistry;

public class Config {

	public List<String> controllerPackages;
	public List<String> fixturePackages;
	public String domainPackage;
	public RateLimit rateLimit;
	public String webroot;
	public String assetsPath;
	public String tplDir;
	public boolean displayErrors;
	public Vertx vertx;
	public AuthProvider authProvider;
	public AuthMethod authMethod;

	public AnnotationProcessorRegistry apRegistry;
	public Map<Class<? extends Annotation>, Set<Handler<RoutingContext>>> annotationHandlers;
	public Map<Class<?>, Processor> typeProcessors;
	public TypedParamInjectorRegistry typeInjectors;
	public AnnotatedParamInjectorRegistry annotInjectors;
	public ServiceRegistry serviceRegistry;
	public RouteRegistry routeRegistry;
	public Map<Class<?>, Handler<RoutingContext>> paramHandlers;
	public Map<String, Handler<RoutingContext>> aopHandlerRegistry;

	/**
	 * TODO : check config instead of throwing exceptions
	 * 
	 * @param json
	 * @return config
	 */
	@SuppressWarnings("unchecked")
	public static Config fromJsonObject(JsonObject json, Vertx vertx) throws MissingConfigurationException {
		Config config = new Config();
		config.vertx = vertx;
		JsonArray controllers = json.getJsonArray("controller-packages");
		if (controllers == null) {
			throw new MissingConfigurationException("controller-packages");
		}
		config.controllerPackages = controllers.getList();
		config.domainPackage = json.getString("domain-package");
		JsonArray fixtures = json.getJsonArray("fixture-packages");
		if (fixtures != null) {
			config.fixturePackages = fixtures.getList();
		} else {
			config.fixturePackages = new ArrayList<String>();
		}
		JsonObject rateLimitJson = json.getJsonObject("throttling");
		if (rateLimitJson != null) {
			int count = rateLimitJson.getInteger("count");
			int value = rateLimitJson.getInteger("time-frame");
			TimeUnit timeUnit = TimeUnit.valueOf(rateLimitJson.getString("time-unit"));
			config.rateLimit = new RateLimit(count, value, timeUnit);
		}
		config.webroot = json.getString("webroot", "web/assets");
		config.assetsPath = json.getString("static-path", "/assets");
		config.tplDir = json.getString("views-dir", "web/views");
		config.displayErrors = json.getBoolean("display-errors", Boolean.FALSE);
		return config;
	}
}
