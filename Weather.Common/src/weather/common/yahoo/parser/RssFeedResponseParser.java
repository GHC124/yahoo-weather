package weather.common.yahoo.parser;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.json.JSONException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import weather.common.yahoo.domain.ChannelResponse;
import weather.common.yahoo.domain.YWResponse;
import weather.common.yahoo.domain.data.ChannelData;
import weather.common.yahoo.net.RssFeedHandler;

public class RssFeedResponseParser extends YWResponseParser {

	@Override
	public YWResponse parse(InputStream is) throws JSONException,
			ParseException, IOException {
		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");

		// Create default handler instance
		RssFeedHandler handler = new RssFeedHandler();
		// Create parser from factory
		XMLReader parser;
		try {
			parser = XMLReaderFactory.createXMLReader();
			// Register handler with parser
			parser.setContentHandler(handler);
			// Create an input source from the XML input stream
			InputSource source = new InputSource(is);
			// parse the document
			parser.parse(source);

			// get data
			ChannelData channelData = handler.getChannelData();

			ChannelResponse channelResponse = new ChannelResponse();
			channelResponse.setChannelData(channelData);

			return channelResponse;

		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
