package weather.common.yahoo.net;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import weather.common.yahoo.domain.data.AstronomyData;
import weather.common.yahoo.domain.data.AtmosphereData;
import weather.common.yahoo.domain.data.ChannelData;
import weather.common.yahoo.domain.data.ConditionData;
import weather.common.yahoo.domain.data.ForecastData;
import weather.common.yahoo.domain.data.ImageData;
import weather.common.yahoo.domain.data.ItemData;
import weather.common.yahoo.domain.data.LocationData;
import weather.common.yahoo.domain.data.UnitsData;
import weather.common.yahoo.domain.data.WindData;
import weather.common.yahoo.parser.data.AstronomyDataParser;
import weather.common.yahoo.parser.data.AtmosphereDataParser;
import weather.common.yahoo.parser.data.ConditionDataParser;
import weather.common.yahoo.parser.data.ForecastDataParser;
import weather.common.yahoo.parser.data.LocationDataParser;
import weather.common.yahoo.parser.data.UnitsDataParser;
import weather.common.yahoo.parser.data.WindDataParser;

/**
 * {@link DefaultHandler} used to parse RSS from Yahoo! Weather RSS Feed
 * 
 * @author ChungPV1
 * 
 */
public class RssFeedHandler extends DefaultHandler {
	private Stack<String> mElementStack;
	private ChannelData mChannelData;

	public RssFeedHandler() {
		mElementStack = new Stack<String>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// Push it in element stack
		mElementStack.push(qName);

		if ("channel".equals(qName)) {
			mChannelData = new ChannelData();
		} else if ("yweather:location".equals(qName)) {
			if (attributes != null) {
				LocationData locationData = LocationDataParser
						.parse(attributes);
				mChannelData.setLocationData(locationData);
			}
		} else if ("yweather:units".equals(qName)) {
			if (attributes != null) {
				UnitsData unitsData = UnitsDataParser.parse(attributes);
				mChannelData.setUnitsData(unitsData);
			}
		} else if ("yweather:wind".equals(qName)) {
			if (attributes != null) {
				WindData windData = WindDataParser.parse(attributes);
				mChannelData.setWindData(windData);
			}
		} else if ("yweather:atmosphere".equals(qName)) {
			if (attributes != null) {
				AtmosphereData atmosphereData = AtmosphereDataParser
						.parse(attributes);
				mChannelData.setAtmosphereData(atmosphereData);
			}
		} else if ("yweather:astronomy".equals(qName)) {
			if (attributes != null) {
				AstronomyData astronomyData = AstronomyDataParser
						.parse(attributes);
				mChannelData.setAstronomyData(astronomyData);
			}
		} else if ("yweather:condition".equals(qName)) {
			ItemData itemData = mChannelData.getItemData();
			if (attributes != null) {
				try {
					ConditionData conditionData = ConditionDataParser
							.parse(attributes);
					itemData.setConditionData(conditionData);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} else if ("yweather:forecast".equals(qName)) {
			ItemData itemData = mChannelData.getItemData();
			if (attributes != null) {
				ForecastData forecastData = null;
				try {
					forecastData = ForecastDataParser.parse(attributes);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (forecastData != null) {
//					List<ForecastData> forecastDataArray = itemData
//							.getForecastData();
//					if (forecastDataArray == null) {
//						itemData.setForecastData(new ArrayList<ForecastData>());
//						forecastDataArray = itemData.getForecastData();
//					}
//					forecastDataArray.add(forecastData);
				}
			}

		} else if ("image".equals(qName)) {
			mChannelData.setImageData(new ImageData());
		} else if ("item".equals(qName)) {
			mChannelData.setItemData(new ItemData());
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// Remove last added element
		mElementStack.pop();
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String value = new String(ch, start, length).trim();
		if (value.length() == 0) {
			return; // ignore white space
		}
		// handle the value based on to which element it belongs
		String currentElement = mElementStack.peek();
		if ("title".equals(currentElement)) {

		}
	}

	public ChannelData getChannelData() {
		return mChannelData;
	}
}
