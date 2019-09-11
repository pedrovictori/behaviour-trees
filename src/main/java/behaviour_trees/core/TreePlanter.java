package behaviour_trees.core;

import behaviour_trees.composite.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.management.modelmbean.XMLParseException;
import javax.swing.text.html.HTML;
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

public class TreePlanter {
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
		this.classLibrary = classLibrary;
		try {
			schema = loadSchema();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		document = getValidatedDocument(xmlStream);
	}

	public Tree plantTree(int id) {
		//find trunk and tree guard if present
		Element root = document.getDocumentElement();

		Tree tree = new Tree(id, parseTask(root, id), parseArgs(root));
		return tree;
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
		if (taskType.equals(TaskType.COMPOSITE.getType())) {
			return parseCompositeTask(element, id);
		} else if (taskType.equals(TaskType.LEAF.getType())) {
			return parseLeafTask(element, id);
		} else {
			throw new IllegalArgumentException("Invalid task type");
		}
	}
	private Guard parseGuard(Element element, int id) {
		if (element.getElementsByTagName(TagName.GUARD.getName()).getLength() == 0) { //has no guard
			return null;
		} else {
			Element guardElement = (Element) element.getElementsByTagName(TagName.GUARD.getName()).item(0);
			Task guard = createInstanceFrom(guardElement, id, parseGuard(guardElement, id), parseArgs(guardElement)); //guard can itself be guarded
			if(guard instanceof Guard) return (Guard) guard;
			else throw new IllegalArgumentException("Invalid class definition");
		}
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
		Class<? extends Task> loadedClass = classLibrary.get(className);
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

	private Schema loadSchema() throws SAXException {
		String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		SchemaFactory factory = SchemaFactory.newInstance(language);
		schema = factory.newSchema((getClass().getClassLoader().getResource("bt.xsd")));
		return schema;
	}
}
