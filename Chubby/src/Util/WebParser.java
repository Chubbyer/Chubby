package Util;

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import Module.Event;
import Module.WebVisiter;

public class WebParser {
	public MongoDBJDBC mongoer;
	public String host;
	public String fileName;
	public WebParser(MongoDBJDBC mongoer,String host,String fileName) {
		// TODO Auto-generated constructor stub
		this.mongoer=mongoer;
		this.host=host;
		this.fileName=fileName;
		mongoer.connectionMongoDB();
	}
	class BookHandler extends DefaultHandler {
		private WebVisiter visiter = new WebVisiter();
		private boolean title = false;
		public long lines = 0L;
		// 用来存放每次遍历后的元素名称(节点名称)
		private String tagName;

		public WebVisiter getVisiter() {
			return visiter;
		}

		public void setVisiter(WebVisiter visiter) {
			this.visiter = visiter;
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
			System.out.println(lines);
			mongoer.closeMongoDB();
			//更新User的信息
			MongoDBJDBC mongoer=MongoDBJDBC.createMongoger("User");
			mongoer.updateUserInfo(host, "Web_Flag", true);
			mongoer.updateUserInfo(host, "WebLogLines", (int)lines);
		}

		/**
		 * Start processing of an element.
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			// Using qualified name because we are not using xmlns prefixes
			// here.
			if (qName.equals("item")) {
				//this.visiter;
				this.lines++;
			}
			this.tagName = qName;
		}

		@Override
		public void endElement(String namespaceURI, String localName,
				String qName) throws SAXException {
			// End of processing current element
			if (qName.equals("item")) {
				mongoer.insertWebVisiter(fileName, visiter);
			}
			this.tagName = null;
		}

		@Override
		public void characters(char[] ch, int start, int length) {
			// Processing character data inside an element
			if (this.tagName != null) {
				String data = new String(ch, start, length);
				if (this.tagName.equals("url")) {
					this.visiter.url=(data);
					this.visiter.index=this.lines;
					// System.out.println(data);
				}
				if(this.tagName.equals("visit_time")){
					this.visiter.visit_time=(data);
				}
				if(this.tagName.equals("visit_count")){
					this.visiter.visit_count=(data);
				}
				if(this.tagName.equals("web_browser")){
					this.visiter.web_browser=(data);
				}
			}
		}

	}

	/*
	 * 把某个主机的日志的Security文件写到数据库中
	 */
	public static void writeToMongo(WebParser webParser) {
		//MongoDBJDBC mongoer=new MongoDBJDBC(host);
		String host=webParser.host;
		String file=webParser.fileName;
		String filePath="D:\\WebLogFiles\\"+host+"\\"+file+".xml";
		XMLReader parser;
		try {
			parser = XMLReaderFactory.createXMLReader();
			BookHandler bookHandler = (webParser).new BookHandler();
			parser.setContentHandler(bookHandler);
			parser.parse(filePath);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("解析出错了！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("文件打开出错了！");
		}finally{
			//.closeMongoDB();
		}
	}

	public static void main(String[] args) throws SAXException, IOException {
//		MongoDBJDBC.connectionMongoDB();
//		XMLReader parser = XMLReaderFactory.createXMLReader();
//		BookHandler bookHandler = (new SAXParser()).new BookHandler();
//		parser.setContentHandler(bookHandler);
//		parser.parse("C:\\Users\\Administrator\\Desktop\\Test\\event.xml");
		// System.out.println(bookHandler.lines);
		// 事务机制
		//MongoDBJDBC.findAll("Chubby", "Security");
		MongoDBJDBC mongoer=MongoDBJDBC.createMongoger("Leung");
		WebParser saxParser=new WebParser(mongoer, "Leung", "WebLogs");
		WebParser.writeToMongo(saxParser);
		//http://jingyan.baidu.com/article/0eb457e51ea6bd03f0a90562.html
		
	}
}
