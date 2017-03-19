import java.net.URL;
import java.net.URLConnection;

public class Core {
	public static void main(String[] args) {
		String zufall = "https://de.wikipedia.org/wiki/Spezial:Zufällige_Seite";
		URL url;

		for (int a = 0; a < 150; a++) {
			try {
				url = new URL(zufall);
				URLConnection con = url.openConnection();
				con.getInputStream();
				Object[][] nodes = getNodes(String.valueOf(con.getURL()).substring(24));
				for (Object[] obj : nodes) {
					System.out.println(
							((WikiNode) obj[0]).getName() + " " + ((WikiNode) obj[1]).getName() + " " + obj[2]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public static Object[][] getNodes(String name) {
		Object[][] ret = new Object[3][3];
		WikiNode firstNode = new WikiNode(name, 0);

		// getNodeChilds(firstNode, 0, new ArrayList<WikiNode>());
		for (int a = 0; a < 3; a++) {
			ret[a][0] = firstNode;
			int level = (int) Math.round(Math.random() * 1 + 2);
			WikiNode nodeWatched = firstNode;
			for (int b = 0; b < level; b++) {
				nodeWatched = (WikiNode) nodeWatched.getNodes()
						.toArray()[(int) Math.round(Math.random() * (nodeWatched.getNodes().toArray().length - 1))];
			}
			if (nodeWatched.getName().length() <= 30 && !firstNode.getName().equals(nodeWatched.getName())) {
				boolean parsed = true;
				for (Object[] obj : ret) {
					if (obj[1] != null && nodeWatched != null) {
						if (((WikiNode) obj[1]).getName().equals(nodeWatched.getName())) {
							parsed = false;
						}
					}
				}
				if (parsed) {
					ret[a][1] = nodeWatched;
					ret[a][2] = level;
				} else {
					a--;
				}
			} else {
				a--;
			}
		}
		return ret;
	}
}
