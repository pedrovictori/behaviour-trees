package behaviour_trees.core;

import behaviour_trees.composite.*;
import behaviour_trees.leaves.Failure;
import behaviour_trees.leaves.Success;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static behaviour_trees.core.BTLogger.*;

public class TreePlanter {
	private static final Map<String, Class<? extends Task>> DEFAULT_LIBRARY = Map.of("Success", Success.class, "Failure", Failure.class);
	private enum TagName {
		ARGS("args"),
		ARG("arg"),
		TASK("task"),
		BRANCHES("branches"),
		GUARD("guard");
		private String name;
		private TagName(String name) {
			this.name = name;
		}

		private String getName() {
			return name;
		}
	}
	private enum TaskType {
		LEAF("leaf"),
		COMPOSITE("composite");

		private String type;
		private TaskType(String type) {
			this.type = type;
		}

		private String getType() {
			return type;
		}
	}
	private enum Attribute {
		TYPE("type"),
		CLASS("class");

		String name;
		Attribute(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
	private enum CompositeClass {
		SELECTOR("selector", Selector.class),
		SEQUENCE("sequence", Sequence.class),
		PARALLEL("parallel", ParallelTask.class),
		RANDOM_SEQUENCE("rnd-sequence", RandomSequence.class),
		RANDOM_SELECTOR("rnd-selector", RandomSelector.class);

		private String name;
		private Class<CompositeTask> classRef;
		private CompositeClass(String name, Class classRef) {
			this.name = name;
			this.classRef = classRef;
		}

		private String getName() {
			return name;
		}

		private Class<CompositeTask> getClassRef() {
			return classRef;
		}
	}
	private Document document;
	private Schema schema;
	private Map<String, Class<? extends Task>> classLibrary;

	public TreePlanter(InputStream xmlStream, Map<String, Class<? extends Task>> classLibrary) {
		debug("Creating new TreePlanter");
		this.classLibrary = classLibrary;
		try {
			schema = loadSchema();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		document = getValidatedDocument(xmlStream);
	}

	public Tree plantTree(int id) {
		debug("planting new Tree");
		//find trunk and tree guard if present
		Element root = document.getDocumentElement();
		return new Tree(id, parseTask(root, id), parseArgs(root));
	}

	private CompositeTask parseCompositeTask(Element element, int id) {
		//int id, List<Task> branches, Guard guard
		String classAttr = element.getAttribute(Attribute.CLASS.getName());
		Class<CompositeTask> cTask = null;
		for (CompositeClass value : CompositeClass.values()) {
			if (classAttr.equals(value.getName())) {
				cTask = value.getClassRef();
			}
		}
		if (cTask == null) {
			cTask = (Class<CompositeTask>) classLibrary.get(classAttr);
		}

		return createCompositeInstance(cTask, id, parseBranches(element, id), parseGuard(element, id), parseArgs(element));
	}

	private List<Task> parseBranches(Element root, int id) {
		List<Task> branches = new ArrayList<>();
		Element branchesRoot = (Element) root.getElementsByTagName(TagName.BRANCHES.getName()).item(0);
		NodeList branchesNodes = branchesRoot.getChildNodes();
		for (int i = 0; i < branchesNodes.getLength(); i++) {
			Element branchElement = (Element) branchesNodes.item(i);
			branches.add(parseTask(branchElement, id));
		}
		return branches;
	}

	private Task parseLeafTask(Element element, int id) {
		return createInstanceFrom(element, id, parseGuard(element, id), parseArgs(element));
	}

	private Task parseTask(Element element, int id) {
		String taskType = element.getAttribute(Attribute.TYPE.getName());
		debug("task type: {}", taskType);
		if (taskType.equals(TaskType.COMPOSITE.getType())) {
			return parseCompositeTask(element, id);
		} else if (taskType.equals(TaskType.LEAF.getType())) {
			return parseLeafTask(element, id);
		} else {
			throw new IllegalArgumentException("Invalid task type");
		}
	}
	private Guard parseGuard(Element element, int id) {
		Element guardElement = getGuard(element);
		if (guardElement != null) {
			debug("parsing guard");
			Task guard = createInstanceFrom(guardElement, id, parseGuard(guardElement, id), parseArgs(guardElement)); //guard can itself be guarded
			if(guard instanceof Guard) return (Guard) guard;
			else throw new IllegalArgumentException("Invalid class definition");
		} else return null;
	}

	private CompositeTask createCompositeInstance(Class<CompositeTask> cClass, int id, List<Task> branches, Guard guard, String... args) {
		Constructor<CompositeTask> constructor = null;
		try {
			constructor = cClass.getConstructor(
					int.class,
					List.class,
					Guard.class,
					String[].class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid class: " + cClass.getName());
		}

		//create new instance and return it
		try {
			return constructor.newInstance(id, branches, guard, args);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid class: " + cClass.getName());
		}
	}

	private Task createInstanceFrom(Element element, int id, Guard guard, String... args) {
		String className = element.getAttribute(Attribute.CLASS.getName());
		debug("parsing element for {}", className);
		Class<? extends Task> loadedClass = classLibrary.getOrDefault(className, DEFAULT_LIBRARY.get(className));
		Constructor<? extends Task> constructor = null;
		try {
			constructor = loadedClass.getConstructor(
					int.class,
					Guard.class,
					String[].class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid class: " + loadedClass.getName());
		}

		//create new instance and return it
		try {
			return constructor.newInstance(id, guard, args);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid class: " + loadedClass.getName());
		}
	}

	private String[] parseArgs(Element element) {
		if (element.getElementsByTagName(TagName.ARGS.getName()).getLength() == 0) { //has no args
			return new String[0];
		} else {
			Element argsElement = (Element) element.getElementsByTagName(TagName.ARGS.getName()).item(0);
			NodeList argNodes = argsElement.getChildNodes();
			String[] args = new String[argNodes.getLength()];
			for (int i = 0; i < argNodes.getLength(); i++) {
				args[i] = argNodes.item(i).getTextContent();
				debug("arg {}: {}", i, args[i]);
			}
			return args;
		}
	}

	private Document getValidatedDocument(InputStream is) {
		DocumentBuilderFactory builderFactory =
				DocumentBuilderFactory.newInstance();
		builderFactory.setSchema(schema);
		builderFactory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder = null;

		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document document = null;
		try {
			document = builder.parse(is);
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}

		Validator validator = schema.newValidator();

		try {
			validator.validate(new DOMSource(document));
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		return document;
	}

	private Element getGuard(Element element){
		for(Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeName().equals(TreePlanter.TagName.GUARD.getName())) {
				return (Element)child;
			}
		}
		return null;
	}

	private Schema loadSchema() throws SAXException {
		String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		SchemaFactory factory = SchemaFactory.newInstance(language);
		schema = factory.newSchema((getClass().getClassLoader().getResource("bt.xsd")));
		return schema;
	}
}
