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

class BookHandler extends DefaultHandler { 
     private List<Event> list; 
     private Event event;
     private boolean title = false; 
     public long lines=0L;
     //用来存放每次遍历后的元素名称(节点名称)  
     private String tagName;
  
     public List<Event> getList() {
		return list;
	}
	public void setList(List<Event> list) {
		this.list = list;
	}
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
        list = new ArrayList<Event>(); 
     } 
     // Called at end of an XML document 
     @Override 
     public void endDocument() throws SAXException {  
        System.out.println("End");  
     } 
     
     /** 
      * Start processing of an element. 
      */ 
     @Override 
     public void startElement(String uri, String localName, String qName, 
	     Attributes atts) throws SAXException { 
        // Using qualified name because we are not using xmlns prefixes here. 
        if (qName.equals("Event")) { 
           //title = true; 
        	event=new Event();
        	System.out.println(atts.getValue(0));
        } 
        this.tagName=qName;
     } 
  
     @Override 
     public void endElement(String namespaceURI, String localName, String qName) 
        throws SAXException { 
        // End of processing current element 
        if (qName.equals("Event")) { 
           //title = false; 
        	this.list.add(event);
        } 
        this.tagName=null;
     } 
  			
     @Override 
     public void characters(char[] ch, int start, int length) { 
        // Processing character data inside an element 
        if (this.tagName!=null) { 
           String data = new String(ch, start, length); 
           //System.out.println("EventID: " + bookTitle); 
           //lines++;
           if(this.tagName.equals("EventID")){
        	   this.event.setEventID(data);
        	   System.out.println(data);
           }
           if(this.tagName.equals("System")){
        	   //this.event.setTimeCreated(attributes.getValue(0));
        	   System.out.println(data);
           }
           if(this.tagName.equals("Computer")){
        	   this.event.setComputer(data);
        	   System.out.println(data);
           }
           //list.add(bookTitle); 
        } 
     } 
			
  } 
	
  public static void main(String[] args) throws SAXException, IOException { 
     XMLReader parser = XMLReaderFactory.createXMLReader(); 
     BookHandler bookHandler = (new SAXParser()).new BookHandler(); 
     parser.setContentHandler(bookHandler); 
     parser.parse("C:\\Users\\Administrator\\Desktop\\Test\\event.xml"); 
     //System.out.println(bookHandler.lines); 
  } 
}
