import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlerController {
	int i =9;
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String crawlStorageFolder = "src/data/crawl/"; 
		CrawlConfig config = new CrawlConfig();
	    config.setCrawlStorageFolder(crawlStorageFolder);
	    
        int numberOfCrawlers = 20;
       
        config.setPolitenessDelay(1000);
        config.setMaxDepthOfCrawling(16);
        config.setMaxPagesToFetch(20000);
        config.setIncludeBinaryContentInCrawling(true);
       
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		robotstxtConfig.setEnabled(false);
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		controller.addSeed("http://www.nydailynews.com/");
		controller.start(MyCrawler.class, numberOfCrawlers);
		
//----------------------------------End----------------------------------------------------------
		System.out.println("ProcessedPages:----"+controller.getFrontier().getNumberOfProcessedPages());
		int unWhiCurr=0;
	    int unWtoCurr=0;
	    Set<String> links = MyCrawler.linksCurr;
		if(!links.isEmpty()) {
   	     Iterator<String> itcurr = links.iterator();
   	     	while(itcurr.hasNext()) {
   	     		String strCurr = itcurr.next().toLowerCase();
   	    	 	if(strCurr.startsWith("http://www.nydailynews.com/")||strCurr.toLowerCase().startsWith("https://www.nydailynews.com/")) {
   	    	 		unWhiCurr=unWhiCurr+1;
   	    	 	}else {
   	    	 		unWtoCurr=unWtoCurr+1;
   	    	 	}
   	     }
      }
		System.out.println(" ");
		System.out.println("Name: Aibo Sun");
		System.out.println("USC ID: 4059865661");
		System.out.println("News site crawled: www.nydailynews.com");
		System.out.println("Fetch Statistics");
		System.out.println("================");
		System.out.println("# fetches attempted:"+MyCrawler.fetchAttNum);
		System.out.println("# fetches succeed:"+MyCrawler.code200);
		System.out.println("# fetches aborted or fetches failed:"+(MyCrawler.fetchAttNum-MyCrawler.code200));
		System.out.println(" ");
		
		System.out.println("Outgoing URLs");
		System.out.println("================");
		System.out.println("# Total URLs extracted:"+MyCrawler.totalLinks);
		System.out.println("# unique URLs extracted:"+MyCrawler.linksCurr.size());
		System.out.println("# unique URLs within News Site:"+unWhiCurr);
		System.out.println("# unique URLs outside News Site:"+unWtoCurr);
		System.out.println(" ");
		
		System.out.println("Status Codes");
		System.out.println("================");
		System.out.println("code2XX------------------------"+MyCrawler.code2XX);
		System.out.println("200 OK: "+MyCrawler.num);
		System.out.println("301 Moved Permanently: "+MyCrawler.code301);
		System.out.println("401 Unauthorized: "+MyCrawler.code401);
		System.out.println("403 Forbidden: "+MyCrawler.code403);
		System.out.println("404 Not Found: "+MyCrawler.code404);
		System.out.println("500 INTERNAL SERVERERROR: "+MyCrawler.failednum);
		System.out.println("ioexcepNum: "+MyCrawler.ioexcepNum);
		
		System.out.println(" ");
		
		System.out.println("File Sizes:");
		System.out.println("================");
		System.out.println("< 1KB: "+MyCrawler.siz1k);
		System.out.println("1KB ~ <10KB: "+MyCrawler.siz10k);
		System.out.println("10KB ~ <100KB: "+MyCrawler.siz100k);
		System.out.println("100KB ~ <1MB: "+MyCrawler.siz1000k);
		System.out.println(">= 1MB: "+MyCrawler.siz1m);
		System.out.println(" ");
		
		System.out.println("Content Types:");
		System.out.println("================");
		System.out.println("text/html: "+MyCrawler.htmlnum);
		System.out.println("image/gif: "+MyCrawler.gifnum);
		System.out.println("image/jpeg: "+MyCrawler.jpegnum);
		System.out.println("image/png: "+MyCrawler.pngnum);
		System.out.println("application/pdf: "+MyCrawler.pdfnum);
		
	
	}

}
