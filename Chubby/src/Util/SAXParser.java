package Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import Module.Event;

public class SAXParser {
	public MongoDBJDBC mongoer;
	public String host;
	public String fileName;
	public int lineSum = 0;

	public SAXParser(MongoDBJDBC mongoer, String host, String fileName) {
		// TODO Auto-generated constructor stub
		this.mongoer = mongoer;
		this.host = host;
		this.fileName = fileName;
		mongoer.connectionMongoDB();
	}

	class BookHandler extends DefaultHandler {
		private Event event = new Event();
		private boolean title = false;
		public long lines = 0L;
		// 用来存放每次遍历后的元素名称(节点名称)
		private String tagName;

		public Event getEvent() {
			return event;
		}

		public void setEvent(Event event) {
			this.event = event;
		}

		public String getTagName() {
			return tagName;
		}

		public void setTagName(String tagName) {
			this.tagName = tagName;
		}

		// Called at start of an XML document
		@Override
		public void startDocument() throws SAXException {
			System.out.println("Start parsing document...");
		}

		// Called at end of an XML document
		@Override
		public void endDocument() throws SAXException {
			System.out.println("Parsing End");
			mongoer.closeMongoDB();
			// 更新User的信息
			MongoDBJDBC mongoer = MongoDBJDBC.createMongoger("User");
			mongoer.updateUserInfo(host, "Flag", true);
			mongoer.updateUserInfo(host, "LogLines", (int) lines);
		}

		/**
		 * Start processing of an element.
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			// Using qualified name because we are not using xmlns prefixes
			// here.
			if (qName.equals("TimeCreated")) {
				this.event.setTimeCreated(TimeParser.getTimeStr(atts
						.getValue(0)));
				mongoer.insertEvent(fileName, this.event);
			}
			this.tagName = qName;
		}

		@Override
		public void endElement(String namespaceURI, String localName,
				String qName) throws SAXException {
			// End of processing current element
			if (qName.equals("EventID")) {
				// this.list.add(event);
			}
			this.tagName = null;
		}

		@Override
		public void characters(char[] ch, int start, int length) {
			// Processing character data inside an element
			if (this.tagName != null) {
				String data = new String(ch, start, length);
				if (this.tagName.equals("EventID")) {
					this.lines++;
					lineSum++;
					this.event.setEventID(data);
					this.event.setI((int) this.lines);
					// System.out.println(data);
				}
			}
		}

	}

	/*
	 * 把某个主机的日志的Security文件写到数据库中
	 */
	public static boolean writeToMongo(SAXParser saxParser) {
		// MongoDBJDBC mongoer=new MongoDBJDBC(host);
		String host = saxParser.host;
		String file = saxParser.fileName;
		String filePath = "D:\\LogFiles\\" + host + "\\" + file + ".xml";
		XMLReader parser;
		try {
			parser = XMLReaderFactory.createXMLReader();
			BookHandler bookHandler = (saxParser).new BookHandler();
			parser.setContentHandler(bookHandler);
			parser.parse(filePath);
			return true;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("解析出错了！");
			if (saxParser.lineSum != 0) {
				// 更新User的信息
				MongoDBJDBC mongoer = MongoDBJDBC.createMongoger("User");
				mongoer.updateUserInfo(host, "Flag", true);
				mongoer.updateUserInfo(host, "LogLines", saxParser.lineSum);
				return true;
			}
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("文件打开出错了！");
			return false;
		} finally {
			// .closeMongoDB();
		}
	}

	public static void main(String[] args) throws SAXException, IOException {
		// MongoDBJDBC.connectionMongoDB();
		// XMLReader parser = XMLReaderFactory.createXMLReader();
		// BookHandler bookHandler = (new SAXParser()).new BookHandler();
		// parser.setContentHandler(bookHandler);
		// parser.parse("C:\\Users\\Administrator\\Desktop\\Test\\event.xml");
		// System.out.println(bookHandler.lines);
		// 事务机制
		// MongoDBJDBC.findAll("Chubby", "Security");
		MongoDBJDBC mongoer = MongoDBJDBC.createMongoger("LiJie");
		SAXParser saxParser = new SAXParser(mongoer, "LiJie", "Security");
		SAXParser.writeToMongo(saxParser);
	}
}
