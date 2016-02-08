package nl.bikeprint.trackaggregate.aggregegationMethods.od;

import java.util.Vector;

import com.infomatiq.jsi.Rectangle;
 
public class Polygon {


	/**
	 * @author smh
	 * aangepast C# algoritme van http://www.developingfor.net/free-code/polygonf
	 */

		private Vector<Punt> pts = new Vector<Punt>();
		private double minx = 0;
		private double miny = 0;
		private double maxx = 0;
		private double maxy = 0;
		private double xlength = 0;
		private double ylength = 0;

		public Polygon(String wkt)  {
			this.pts.clear();
			String pointsString = wkt.substring(9, wkt.length() - 3 );
			int pos = wkt.indexOf("),(");
			if (pos > 0) {
				pointsString = pointsString.substring(0, pos);
			}
			String[] pointsVector = pointsString.replaceAll(", ", ",").split(",");
			for( int i = 1; i < pointsVector.length; i++ ) { 
				// beginnen op 1 ipv 0, WKT gaat rond, eerste en laatste punt dienen
				// niet gelijk aan elkaar te zijn in de vector met punten
				String[] xy =  pointsVector[i].split(" ");
				double x = Double.valueOf(xy[0]);
				double y = Double.valueOf(xy[1]);
				Punt pt = new Punt( x, y );
				this.pts.add(pt);
			}
			this.init();
		}

		
		public Polygon(Vector<Punt> pts) {
			this.pts = pts;
			this.init();
		}
		
		public void init() {
			minx = pts.get(0).x;
			maxx = pts.get(0).x;
			miny = pts.get(0).y;
			maxy = pts.get(0).y;

			for(int i = 0; i < numberOfPoints(); i++)	{
				Punt pt = pts.get(i);
				if (pt.x < minx)
				{
					minx = pt.x;
				}

				if (pt.x > maxx)
				{
					maxx = pt.x;
				}

				if (pt.y < miny)
				{
					miny = pt.y;
				}

				if (pt.y > maxy)
				{
					maxy = pt.y;
				}
			}

			xlength = Math.abs(maxx - minx);
			ylength = Math.abs(maxy - miny);
		}

		/// <summary>
		/// The Rectangular Bounds of the Polygon.
		/// </summary>
		public Rectangle getBounds() {
			return new Rectangle((float)minx, (float)(miny), (float)maxx, (float)maxy);					
		}

		/// <summary>
		/// The Minimum X coordinate value in the Punt collection.
		/// </summary>
		public double getMinX() {
			return minx;
		}

		/// <summary>
		/// The Maximum X coordinate value in the Punt collection.
		/// </summary>
		public double getMaxX() {
			return maxx;
		}

		/// <summary>
		/// The Minimum Y coordinate value in the Punt collection.
		/// </summary>
		public double getMinY() {
			return miny;
		}

		/// <summary>
		/// The Maximum Y coordinate value in the Punt collection.
		/// </summary>
		public double getMaxY(){
			return maxy;
		}

		/// <summary>
		/// The number of Points in the Polygon.
		/// </summary>
		public int numberOfPoints() {
			return pts.size();
		}

		/// <summary>
		/// Compares the supplied punt and determines whether or not it is inside the Rectangular Bounds
		/// of the Polygon.
		/// </summary>
		/// <param name="pt">The Punt to compare.</param>
		/// <returns>True if the Punt is within the Rectangular Bounds, False if it is not.</returns>
		public boolean isInBounds(double x, double y) {
			boolean in = (x >= getMinX() & x <= getMaxX() &
					y >= getMinY() & y <= getMaxY() );
			return in;
		}

		/// <summary>
		/// Compares the supplied punt and determines whether or not it is inside the Actual Bounds
		/// of the Polygon.
		/// </summary>
		/// <remarks>The calculation formula was converted from the C version available at
		/// http://www.ecse.rpi.edu/Homepages/wrf/Research/ShortNotes/pnpoly.html
		/// </remarks>
		/// <param name="pt">The Punt to compare.</param>
		/// <returns>True if the Punt is within the Actual Bounds, False if it is not.</returns>
		public boolean contains(double x, double y) {
			boolean isIn = false;
            double xi, yi, xj, yj;
			if (isInBounds(x, y)) {
				int j = numberOfPoints() - 1;

				// The following code is converted from a C version found at
				// http://www.ecse.rpi.edu/Homepages/wrf/Research/ShortNotes/pnpoly.html
				for (int i = 0; i < numberOfPoints(); i++) {
					xi = pts.get(i).x;
					yi = pts.get(i).y;
					xj = pts.get(j).x;
					yj = pts.get(j).y;
					j = i;
					if (
							(
									(
											(yi <= y) && (y < yj)
									) || (
											(yj <= y) && (y < yi)
									)
							) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi)
						) {
						isIn = !isIn;
			//			System.out.println(isIn);
					}
				}
			}

			return isIn;
		}
/*
		public boolean contains(Point pt) {
			boolean isIn = false;

			//if (isInBounds(x, y)) {
				int j = numberOfPoints() - 1;

				// The following code is converted from a C version found at
				// http://www.ecse.rpi.edu/Homepages/wrf/Research/ShortNotes/pnpoly.html
				for (int i = 0; i < numberOfPoints(); i++) {
					j = i;
					if (
							(
									(
											(pts.get(i).y <= pt.y) && (pt.y < pts.get(j).y)
									) || (
											(pts.get(j).y <= pt.y) && (pt.y < pts.get(i).y)
									)
							) && (pt.x < (pts.get(j).x - pts.get(i).x) * (pt.y - pts.get(i).y) / (pts.get(j).y - pts.get(i).y) + pts.get(i).x)
						) {
						isIn = !isIn;
						System.out.println(isIn);
					}
				}
			//}

			return isIn;
		}
*/
		/// <summary>
		/// Returns the Punt that represents the center of the Rectangular Bounds of the Polygon.
		/// </summary>
		public Punt getCenterPointOfBounds() {
			double x = minx + (xlength / 2);
			double y = miny + (ylength / 2);
			return new Punt(x, y);
		}

		/// <summary>
		/// Calculates the Area of the Polygon.
		/// </summary>
		public double getArea() {
			double xy = 0;
			for (int i = 0; i < numberOfPoints(); i++)
			{
				Punt pt1;
				Punt pt2;
				if (i == numberOfPoints() - 1)
				{
					pt1 = pts.get(i);
					pt2 = pts.get(0);
				}
				else
				{
					pt1 = pts.get(i);
					pt2 = pts.get(i + 1);
				}
				xy += pt1.x * pt2.y;
				xy -= pt1.y * pt2.x;
			}

			double area = Math.abs(xy) * .5;

			return area;
		}

		 

 

}
