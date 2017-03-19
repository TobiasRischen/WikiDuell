import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiNode implements Comparable<WikiNode> {
	private Set<WikiNode> adjacentNodes;
	private final String URL;
	private final int level;

	public WikiNode(String urlSuffix, int level) {
		this.URL = urlSuffix;
		this.level = level;
		this.adjacentNodes = new TreeSet<WikiNode>();
		if (level < 3) {
			
			int linksUsed = 0;
			GetSiteContent gsc = new GetSiteContent();
			String siteContent = gsc.getSiteContent(urlSuffix);
			Set<String> links = this.getLinks(siteContent);
			int numberOfLinks = links.size()<3?links.size():3;
			int trials = 0;
			/*
			 * for (String link : links) { if (linksUsed <= numberOfLinks ) {
			 * WikiNode newNode = this.getNode(link); if (newNode != null) {
			 * this.adjacentNodes.add(newNode); linksUsed++; } } }
			 */
			while (linksUsed <= numberOfLinks && trials <numberOfLinks*100) {
				WikiNode newNode = this
						.getNode((String) links.toArray()[(int) (Math.round(Math.random() * links.size()-0.5))]);
				if (newNode != null) {
					boolean isadded = this.adjacentNodes.add(newNode);
					if(isadded) {linksUsed++;}
				}
				trials++;
			}

		}
	}

	private WikiNode getNode(String nodeUrl) {
		nodeUrl = nodeUrl.substring(nodeUrl.indexOf("href=\"") + 6);
		nodeUrl = nodeUrl.substring(0, nodeUrl.indexOf("\""));
		if (nodeUrl.startsWith("/wiki/")) {
			if(!(Pattern.matches("/wiki/"+"[a-zA-Z]*"+":" + ".*", nodeUrl))) {
				return new WikiNode(nodeUrl, this.level + 1);
			}
			else {
				return null;
			}
			
		} else {
			return null;
		}
	}

	private Set<String> getLinks(String code) {
		Pattern linkPattern = Pattern.compile("(<a[^>]+>.+?</a>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher pageMatcher = linkPattern.matcher(code);
		Set<String> links = new TreeSet<String>();
		while (pageMatcher.find()) {
			links.add(pageMatcher.group());
		}
		return links;
	}

	public Set<WikiNode> getNodes() {
		return this.adjacentNodes;
	}

	public String getName() {
		return this.URL;
	}

	@Override
	public int compareTo(WikiNode arg0) {
		return this.URL.compareTo(arg0.getName());
	}
}
