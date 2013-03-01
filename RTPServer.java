import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import java.net.*;
import java.net.InetAddress;

import javax.media.*;
import javax.media.rtp.*;
import javax.media.Control;
import javax.media.ProcessorModel;
import javax.media.format.*;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;

public class RTPServer extends JPanel implements ActionListener
{
	Processor processor = null;

	public RTPServer()
	{
		setPreferredSize(new Dimension(400,400));
		
		JButton playButton = new JButton("Play");
		playButton.setActionCommand("play");
		playButton.addActionListener(this);
		add(playButton);
		
		JButton stopButton = new JButton("Stop");
		stopButton.setActionCommand("stop");
		stopButton.addActionListener(this);
		add(stopButton);

		try
		{
			// create the RTP Manager
			RTPManager rtpManager = RTPManager.newInstance();
	
			// create the local endpoint for the local interface on
			// any local port
			SessionAddress localAddress = new SessionAddress();
			
			// initialize the RTPManager
			rtpManager.initialize( localAddress);
	
			// create a multicast address for 224.1.1.0 and ports 3000/3001
			InetAddress ipAddress = InetAddress.getByName( "224.1.1.0");

			SessionAddress multiAddress = new SessionAddress( ipAddress, 8080);
			
			// initialize the RTPManager
			rtpManager.initialize( multiAddress);
			
			// add the target
			rtpManager.addTarget( multiAddress);
	
			// create a send stream for the output data source of a processor
			DataSource dataInput = Manager.createDataSource(new MediaLocator(new URI("file:samples/atime.mov").toURL() ));

			processor = Manager.createProcessor(dataInput);
			processor.configure();

			while ((processor.getState() != Processor.Configured))
			{
				try
				{
					Thread.sleep(100);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			processor.setContentDescriptor(new ContentDescriptor(ContentDescriptor.RAW_RTP));
			processor.realize();

			while ((processor.getState() != Controller.Realized))
			{
				try
				{
					Thread.sleep(100);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			processor.setStopTime(processor.getDuration());
			
			System.out.println(processor.getDuration().getSeconds());

			DataSource dataOutput = processor.getDataOutput();

			SendStream sendStream = rtpManager.createSendStream( dataOutput, 1);

			sendStream.start();
			processor.start();
			
			// call dispose at the end of the life-cycle of this RTPManager so
			// it is prepared to be garbage-collected.
			//rtpManager.dispose();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent ex)
	{
		if ("start".equals(ex.getActionCommand()))
		{
			processor.start();
		}
		else if ("stop".equals(ex.getActionCommand()))
		{
			processor.stop();
		}
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.getContentPane().add(new RTPServer());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
} //RTPServer