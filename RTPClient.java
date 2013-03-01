import java.awt.*;
import javax.swing.*;
import java.io.*;

import java.net.*;
import java.net.InetAddress;

import javax.media.*;
import javax.media.rtp.*;
import javax.media.ProcessorModel;
import javax.media.format.*;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;

public class RTPClient extends JPanel
{
	Player player = null;

	public RTPClient()
	{
		setPreferredSize(new Dimension(400,400));
		
		MediaLocator media = null;
		try
		{
			media = new MediaLocator("rtp://224.1.1.0:8080");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (media == null)
		{
			System.out.println("Cannot build MediaLocator for RTP Stream");
		}

		try
		{
			player = Manager.createPlayer(media);
		}
		catch (NoPlayerException e)
		{
			System.out.println("Error: " + e);
		}
		catch (MalformedURLException e)
		{
			System.out.println("Error: " + e);
		}
		catch (IOException e)
		{
			System.out.println("Error: " + e);
		}
		
		player.realize();
		while ((player.getState() != Controller.Realized))
		{
			//
		}

		Component c = player.getVisualComponent();
		add(c);
		player.start();
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.getContentPane().add(new RTPClient());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
} //RTPClient
