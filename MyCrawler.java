import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;

import com.csvreader.CsvWriter;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.url.WebURL;


public class MyCrawler extends WebCrawler {
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|json|jpg"
            + "|mp3|mp3|zip|gz))$");
//	private final static Pattern FILTERS = Pattern.compile(".*(\\.(html|pdf|doc|gif"
//            + "|jpeg|png))$");
    private final static String CSV_PATH = "src/data/visit.csv"; 
    private final static String CSV_FETCH_PATH = "src/data/fetch.csv";
    private File csv;
    private CsvWriter cw;
    private File csvFetch;
    private CsvWriter cwFetch;
	public  static int num=0; 
	public  static int ioexcepNum=0;
	
	public  static int fetchAttNum=0; 
	public  static int htmlnum=0; 
	public  static int pdfnum=0; 
	public  static int gifnum=0; 
	public  static int jpegnum=0; 
	public  static int pngnum=0; 
	
	public  static int siz1k=0; 
	public  static int siz10k=0; 
	public  static int siz100k=0;
	public  static int siz1000k=0;
	public  static int siz1m=0;
	
	public  static int code2XX=0;
	public  static int code200=0;
	public  static int code301=0;
	public  static int code401=0;
	public  static int code403=0;
	public  static int code404=0;
	public  static int failednum=0;
	
	public  static Set<String> linksCurr;
	public  static int totalLinks=0;
	private PageFetcher pageFetcher;

	 public MyCrawler() throws IOException {  
		 linksCurr= new HashSet<String>();
		 CrawlConfig config = new CrawlConfig(); // 实例化爬虫配置
		 pageFetcher = new PageFetcher(config);
		   csv = new File(CSV_PATH);  
		   csvFetch = new File(CSV_FETCH_PATH); 
	        if (csv.isFile()) {  
	            csv.delete();  
	        }  
	        if (csvFetch.isFile()) {  
	        		csvFetch.delete();  
	        }
	        cw = new CsvWriter(new FileWriter(csv, true), ',');
	        cw.write("url");  
	        cw.write("size");  
	        cw.write("outlinks");  
	        cw.write("contentType"); 
	        cw.endRecord();  
	        cw.close();  
	        
	        cwFetch = new CsvWriter(new FileWriter(csvFetch, true), ',');
	        cwFetch.write("url");  
	        cwFetch.write("statuscode");  
	        cwFetch.endRecord();  
	        cwFetch.close();  
	        
	    }  
	 protected WebURL handleUrlBeforeProcess(WebURL curURL) {
		 
		    PageFetchResult fetchResult = null;
		    String contentType="";
		    int statusCode=0;
		    try {
				fetchResult = pageFetcher.fetchPage(curURL);
				statusCode = fetchResult.getStatusCode();
				contentType =fetchResult.getEntity() == null ? "" : 
			            	  fetchResult.getEntity().getContentType() == null ? "" : fetchResult.getEntity().getContentType().getValue();
				if(!contentType.equals("")&&contentType.indexOf(";")>0) {
					contentType=contentType.substring(0, contentType.indexOf(";"));
				}
		    } catch (Exception e) {
		    		failednum = failednum+1;
		        e.printStackTrace();
		        return null;
		    } finally {
		        if (fetchResult != null) { // 假如jvm没有回收 用代码回收对象 防止内存溢出
			          fetchResult.discardContentIfNotConsumed(); // 销毁获取内容
			    }
	
		    }
		   
		       if(statusCode>=200&&statusCode<=299) {
					code2XX=code2XX+1;
					if(statusCode==200) {
						code200 = code200+1;
					}else {
						System.out.println("?????????????????????"+statusCode);
					}
				}else if(statusCode==HttpStatus.SC_MOVED_PERMANENTLY) {
	        	  		code301=code301+1;
		        }else if(statusCode==HttpStatus.SC_UNAUTHORIZED) {
		        	  	code401=code401+1;
		        }else if(statusCode==HttpStatus.SC_FORBIDDEN) {
		        	  	code403=code403+1;
		        }else if(statusCode==HttpStatus.SC_NOT_FOUND) {
		        	  	code404=code404+1;
		        }
		        try {
					cwFetch = new CsvWriter(new FileWriter(csvFetch, true), ',');
					cwFetch.write(curURL.getURL());  
			 	    cwFetch.write(""+fetchResult.getStatusCode());  
			 	    cwFetch.endRecord();  
			 	    cwFetch.close(); 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        fetchAttNum = fetchAttNum+1;
		        if("text/html".equals(contentType)||"application/pdf".equals(contentType)||"image/gif".equals(contentType)||"image/jpeg".equals(contentType)||"image/png".equals(contentType)){
		        		return curURL;
			    }else {
			    		return null;
			    }
	 }
//	You need to create a crawler class that extends WebCrawler. 
//	This class decides which URLs should be crawled and handles the downloaded page. 
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url){
		String href = url.getURL().toLowerCase();
		return!FILTERS.matcher(href).matches()
			&& (href.startsWith("http://www.nydailynews.com/")||href.startsWith("https://www.nydailynews.com/"));
	}
	/**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
	@Override
	public void visit(Page page) {//---200
	   num=num+1;
	   String url = page.getWebURL().getURL();
	   System.out.println(num+"--URL--- ");	
	   if (page.getParseData() instanceof HtmlParseData || page.getParseData() instanceof BinaryParseData) {
	       ParseData parseData;
	       String html;
	       if(page.getParseData() instanceof HtmlParseData) {
	    	   	parseData = (HtmlParseData) page.getParseData();
	    	    html = ((HtmlParseData)parseData).getHtml();
	       }else {
	    	   	parseData = (BinaryParseData) page.getParseData();
	    	    html = ((BinaryParseData)parseData).getHtml();
	       }
	       String contentType= page.getContentType();
	       if(!contentType.equals("")&&contentType.indexOf(";")>0) {
	    	   		contentType=contentType.toLowerCase().substring(0, contentType.indexOf(";"));
	       }
	      
	       long htmlsize = html.length();
	       Set<WebURL> links = parseData.getOutgoingUrls();
//----------------CSV
	       try {
	    	   	cw = new CsvWriter(new FileWriter(csv, true), ',');
	    	    cw.write(url);
	        cw.write(""+html.length());
	        cw.write(""+links.size());
	    	    cw.write(contentType);
	        cw.endRecord();
	    	    cw.flush();
	    	    cw.close();
	    	    } catch (final IOException e) {
	    	    	  ioexcepNum=ioexcepNum+1;
	    	    }
	       
//--------- Unique
	       if(!links.isEmpty()) {
	    	     Iterator<WebURL> it = links.iterator();
	    	     	while(it.hasNext()) {
	    	    	 	linksCurr.add((String) it.next().getURL());
	    	     }
	       }
	     
	       totalLinks = totalLinks +links.size();
	     
	       
//------text/html image/gif image/jpeg  image/png  application/pdf
	       
	       if(contentType.equals("text/html")) {
	    	   	htmlnum=htmlnum+1;
	       }else if(contentType.equals("application/pdf")){
	    	   	pdfnum=pdfnum+1;
	       }else if(contentType.equals("image/gif")) {
	    	   	gifnum=gifnum+1;
	       }else if(contentType.equals("image/jpeg")) {
	    	   	jpegnum = jpegnum+1;
	       }else if(contentType.equals("image/png")) {
	    	   	pngnum = pngnum+1;
	       }else{
	    	   	System.out.println(contentType);
	       }
	       
//------htmlsize	       
	       
	       long htmlsizecurrent = htmlsize/1000;
	       if(htmlsizecurrent<1) {
	    	   	 siz1k=siz1k+1;
	       }else if(htmlsizecurrent>=1&&htmlsizecurrent<10){
	    	   	 siz10k=siz10k+1;
	       }else if(htmlsizecurrent>=10&&htmlsizecurrent<100){
	    	   	siz100k=siz100k+1;
	       }else if(htmlsizecurrent>=100&&htmlsizecurrent<1000){
	    	   	siz1000k=siz1000k+1;
	       }else {
	    	    siz1m=siz1m+1;
	       }
	       
	   }
	 
	}
	
}
