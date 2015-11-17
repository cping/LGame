package loon.action.map.tmx.objects;

import java.util.ArrayList;
import java.util.List;

import loon.utils.xml.XMLElement;

public class TMXPolyLine
{
    private List<TMXPoint> points;

    public TMXPolyLine()
    {
        points = new ArrayList<>();
    }

    public TMXPoint getPoint(int index)
    {
        return points.get(index);
    }

    public List<TMXPoint> getPoints()
    {
        return points;
    }

    public int getNumPoints()
    {
        return points.size();
    }

    public void parse(XMLElement element)
    {
        String pointsLine = element.getAttribute("points","").trim();

        for (String token : pointsLine.split(" "))
        {
            String[] subTokens = token.split(",");

            TMXPoint point = new TMXPoint();
            point.x = Integer.parseInt(subTokens[0].trim());
            point.y = Integer.parseInt(subTokens[1].trim());

            points.add(point);
        }
    }
}
