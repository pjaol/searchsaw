package com.pjaol.ESB.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pjaol.ESB.core.Controller;
import com.pjaol.ESB.core.Evaluator;
import com.pjaol.ESB.core.Module;
import com.pjaol.ESB.core.PipeLine;

public class Initializer {

	private Logger _logger = Logger.getLogger(getClass());
	private ESBCore core = ESBCore.getInstance();
	private Map<String, Controller> uris = new HashMap<String, Controller>();

	/**
	 * Using Components
	 * @throws ConfigurationException
	 */
	public void startup() throws ConfigurationException {

		// Initialize and put pipelines in the core
		try {
			core.setPipelines(startupPipes(core.getPipeLineComponent()));
		} catch (InstantiationException e) {
			throw new ConfigurationException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ConfigurationException(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException(e.getMessage());
		}

		// Initialize and put controllers in the core
		try {
			core.setControllers(startupControllers(core
					.getControllerComponent()));
		} catch (InstantiationException e) {
			throw new ConfigurationException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ConfigurationException(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException(e.getMessage());
		}

		// uris are created in startupControllers
		core.setControllerUris(uris);

	}

	private Map<String, PipeLine> startupPipes(
			Map<String, PipeLineComponent> items)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		Map<String, PipeLine> result = new HashMap<String, PipeLine>();

		for (PipeLineComponent c : items.values()) {
			// System.out.println(c);
			PipeLine pipeline = new PipeLine();
			pipeline.setName(c.getName());

			Evaluator evaluator = initializeEvaluator(c.getEvaluator());
			pipeline.setEvaluator(evaluator);
			pipeline.setTimeout(c.getTimeout());

			List<ConfigurationComponent> modules = c.getModules();
			List<Module> pipeModules = new ArrayList<Module>();
			for (ConfigurationComponent mod : modules) {

				Module m = initializeModule(mod);
				pipeModules.add(m);
			}
			pipeline.setModules(pipeModules);

			result.put(c.getName(), pipeline);
		}

		return result;
	}

	private Map<String, Controller> startupControllers(
			Map<String, ControllerComponent> items)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		Map<String, Controller> result = new HashMap<String, Controller>();

		for (ControllerComponent controllerComponent : items.values()) {
			Controller controller = initializeController(controllerComponent);
			result.put(controllerComponent.getName(), controller);
			uris.put(controllerComponent.getUri(), controller);
		}

		return result;
	}

	private Controller initializeController(
			ControllerComponent controllerComponent)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		String className = controllerComponent.getClassName();
		_logger.info("Initializing controller: "
				+ controllerComponent.getName() + " : " + className);
		Controller controller = extracted(className);
		controller.setName(controllerComponent.getName());
		controller.setPipelines(controllerComponent.getPipelines());
		controller.setPipes(controllerComponent.getPipes());
		controller.setUri(controllerComponent.getUri());
		controller.setLimitorPipeLines(controllerComponent
				.getLimitorPipeLines());
		controller.setLimitorName(controllerComponent.getLimitorName());
		controller.setTimeout(controllerComponent.getTimeout());

		controller.init(controllerComponent.getArgs());
		return controller;

	}

	private Evaluator initializeEvaluator(EvaluatorComponent evaluatorComponent)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		String className = evaluatorComponent.getClassName();
		_logger.info("Initializing evaluator: " + evaluatorComponent.getName()
				+ " : " + className);
		Evaluator e = extracted(className);
		e.setName(evaluatorComponent.getName());
		e.init(evaluatorComponent.getArgs());

		return e;
	}

	private Module initializeModule(ConfigurationComponent module)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		String className = module.getClassName();
		_logger.info("Initializing Module: " + module.getName() + " : "
				+ className);
		Module m = extracted(className);
		m.setName(module.getName());
		m.init(module.getArgs());

		return m;
	}

	private <T> T extracted(String className) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		return (T) Class.forName(className).newInstance();
	}
}
