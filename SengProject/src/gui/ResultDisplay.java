package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import New.lecMS;
import Selecting_Algothrim.newMomentum;
import Selecting_Algothrim.orderObject;
import Selecting_Algothrim.signalObject;
import Trading_Engine.MyAskList;
import Trading_Engine.MyBidList;
import Trading_Engine.ResultData;
import Trading_Engine.myDatabase;

public class ResultDisplay extends JFrame {
	public static myDatabase myDB; 
	public ResultDisplay(String frametitle, myDatabase db){
		myDB = db;
		JTabbedPane jtb = new JTabbedPane();
		Container con = this.getContentPane(); 
		con.add(jtb);
		
		setTitle(frametitle); 
		setSize(800,600);
		setLocationRelativeTo(null);

		setResizable(false);

		JMenuBar menubar = new JMenuBar();

		JMenu file = new JMenu("File");
		JMenuItem quit = new JMenuItem("Close");

		menubar.add(file);
		file.add(quit);

		JMenu strategy = new JMenu("Strategy");
		JMenuItem momstrategy = new JMenuItem("Run Momentum Strategy");
		JMenuItem revstrategy = new JMenuItem("Run Momentum Reversion Strategy");
		strategy.add(momstrategy);
		strategy.add(revstrategy);
		menubar.add(strategy);
		setJMenuBar(menubar);

		jtb.addTab("Analysis", analysisPanel());
		jtb.addTab("Orderbook", orderbookPanel());
		jtb.addTab("Graph", graphPanel());

		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setVisible(false);
				dispose();
			}
		});

		momstrategy.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {	
						//open new frame for strategy analysis
						myStrategyResult = new StrategySelected();
						myStrategyResult.setVisible(true);
						//run strategy to update the new frame's table
						//include all data changes here, i.e graph
						runNewStrategy();					
					}
				}
				);	
		setVisible(true);
	}

	public JLabel LinesRead;
	public JLabel MatchedLines;
	public JLabel UpdatedLines;
	public JLabel DeletedLines;
	public JLabel BidList;
	public JLabel AskList;

	private JPanel analysisPanel() {
		JPanel toppanel = new JPanel();
		JPanel analysispanel = new JPanel();
		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel();
		
		Dimension d = new Dimension(150,100);
		panel.setLayout((new BoxLayout(panel, BoxLayout.PAGE_AXIS)));
		
		panel.setSize(d);
		panel.setPreferredSize(d);
		panel.setMaximumSize(d);
		
		panel2.setLayout((new BoxLayout(panel2, BoxLayout.PAGE_AXIS)));
		panel2.setSize(d);
		panel2.setPreferredSize(d);
		panel2.setMaximumSize(d);
		
		
		LinesRead = new JLabel("lines read");
		MatchedLines = new JLabel("matched");
		UpdatedLines = new JLabel("updated");
		DeletedLines = new JLabel("delete text");
		BidList = new JLabel("total text");
		AskList = new JLabel("tradelines text");

		panel.add(new JLabel("Total lines read:"));
		panel.add(new JLabel("Total lines matched:"));	
		panel.add(new JLabel("Total lines update:"));	
		panel.add(new JLabel("Total lines deleted:"));
		panel.add(new JLabel("Bid list contains"));	
		panel.add(new JLabel("Ask list contains"));

		panel2.add(LinesRead);
		panel2.add(MatchedLines);
		panel2.add(UpdatedLines);
		panel2.add(DeletedLines);
		panel2.add(BidList);
		panel2.add(AskList);
		
		analysispanel.add(panel);
		analysispanel.add(panel2);
		
		toppanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;	
		analysispanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		toppanel.add(analysispanel,c);
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 2;
		c.gridx = 1;
		c.gridy = 0;
		toppanel.add(new JPanel(),c);
		
		return toppanel;
	}

	private OrderbookTable ordertable;

	private JPanel orderbookPanel() {
		JPanel panel = new JPanel();
		ordertable = new OrderbookTable();
		
		//Date date= null;
		
		JTable buybook = new JTable();

		buybook.setModel(ordertable);
		/*
		String originaltimestamp = "2010-07-14 09:00:02";
		String timestamp = new SimpleDateFormat("HH:mm").format(date); // 9:00
				
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(originaltimestamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		*/
		System.out.println("sdfsdfsd");
		Object [] fakedata1 = {123,245, "$ " + Double.toString(new Double(5.30)), new Integer(43), "9:00"};
		
		ordertable.addElement(fakedata1);
		ordertable.addElement(fakedata1);
		ordertable.addElement(fakedata1);
		ordertable.addElement(fakedata1);

		Dimension d = new Dimension (500,150);

		JScrollPane scrollTable = new JScrollPane(buybook);
		buybook.setFillsViewportHeight(true);
		scrollTable.setPreferredSize(d);
		scrollTable.setMaximumSize(d);	
		
		panel.add(scrollTable);
		return panel;
	}

	private JPanel graphPanel(){

		JPanel panel = new JPanel();
		JPanel graphpanel = new JPanel();
		
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//create new graph and data set
		LineGraph returntimegraph = new LineGraph("Trades without Matching");
		//add plots to the graph
		LineGraph.addToDataset(200.0, 1.2);
		LineGraph.addToDataset(230.0, 1.3);
		LineGraph.addToDataset(300.0, 1.4);
		LineGraph.addToDataset(400.0, 3.2);
		LineGraph.addToDataset(810.0, 2.3);
		LineGraph.addToDataset(240.0, 4.3);
		LineGraph.addToDataset(546.0, 4.6);
		//loop through database
		
		//finalise dataset for graph
		returntimegraph.finishGraph();
		returntimegraph.setVisible(true);
		graphpanel.add(returntimegraph);
		
		//Configurations for graph, not sure if can be done

		JPanel config = new JPanel();
		config.setLayout((new BoxLayout(config, BoxLayout.PAGE_AXIS)));
		config.add(new JLabel("Configure Display"));
		JButton updategraph = new JButton("Update");
		updategraph.setSize(new Dimension(150,75));
		config.add(updategraph);
		config.setBorder(new EmptyBorder(0, 0, 0, 100)) ;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(graphpanel,c );
		c.gridx = 2;
		c.gridy = 1;
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 2;
		//panel.add(config);  <--- uncomment to display on gui
			
		return panel;
	}
	private StrategySelected myStrategyResult; 
	protected void runStrategy() {
		try {
			ResultSet set = myDB.getResultSet("SELECT * FROM all_list;");
			if(set != null){
				int count = 0;
				String tmp;
				String tmpType;
				double tmpPrice;
				int tmpVol;
				long tmpID = 0;
				int buySig = 0;
				int sellSig = 0;
				double profit = 0;
				int tmpCount1 = 0;
				int tmpCount2 = 0;
				newMomentum moment = new newMomentum();
				signalObject tempSignal;
				signalObject lastBuySig = null;
				orderObject lastSale = new orderObject( -1, -1);
				orderObject lastBuy = new orderObject( -1, -1);
				myDB.initTwoList();
				while (set.next()){
					tmp = set.getString(5);
					tmpType = set.getString(14);
					if(tmp.equalsIgnoreCase("ENTER")){
						tmpPrice = set.getDouble(6);
						tmpVol = set.getInt(7);
						moment.addTrade(tmpPrice);
						tempSignal = moment.generateOrderSignal(lastSale, lastBuy);
						if(tmpType.equalsIgnoreCase("B")){
							tmpID = set.getLong(12);
							lastBuy = new orderObject(tmpVol,tmpPrice);
							myDB.insertBidList(tmpID,tmpPrice,tmpVol);
						}else if(tmpType.equalsIgnoreCase("A")){
							tmpID = set.getLong(13);
							lastSale = new orderObject(tmpVol,tmpPrice);
							myDB.insertAskList(tmpID,tmpPrice,tmpVol);
						}
						if(tempSignal.getType().equalsIgnoreCase("buy")){
							buySig++;
							moment.getreceiptNumber(buySig);
							lastBuySig = tempSignal;
							//System.out.println("buy generated - count " + count);
						}else if(tempSignal.getType().equalsIgnoreCase("sell")){
							moment.getreceiptNumber(sellSig);
							profit += (tempSignal.getPrice() - lastBuySig.getPrice());
							sellSig++;
							//System.out.println("sell generated - count " + count);
						}
					}else if (tmp.equalsIgnoreCase("AMEND")){

						tmpPrice = set.getLong(12);
						tmpVol = set.getInt(7);
						if(tmpType.equalsIgnoreCase("B")){
							tmpID = set.getLong(12);
							myDB.updateBidList(tmpID, tmpPrice, tmpVol);
						}else if(tmpType.equalsIgnoreCase("A")){
							tmpID = set.getLong(13);
							myDB.updateBidList(tmpID, tmpPrice, tmpVol);
						}
					}else if (tmp.equalsIgnoreCase("DELETE")){

						if(tmpType.equalsIgnoreCase("B")){
							tmpID = set.getLong(12);
							myDB.deleteOneFromList(tmpID, "bid_list");
						}else if(tmpType.equalsIgnoreCase("A")){
							tmpID = set.getLong(13);
							myDB.deleteOneFromList(tmpID, "ask_list");
						}
					}
					count++;
				};
				System.out.println("count : " + count);
				Mainmenu.console.append("Total lines read : " + count + "\n");
				Mainmenu.console.append("Strategy generate " + buySig + " buy signals.\n");
				Mainmenu.console.append("Strategy generate " + sellSig + " sell signals.\n");
				Mainmenu.console.append("Profit gain: " + profit + "\n");
				myDB.printTwoList();
				myDB.closeTwoList();
			}else{
				System.out.println("set equals null");
			}
			ResultSet bidleft = myDB.getResultSet("SELECT count(*) FROM bid_list;");
			ResultSet askleft = myDB.getResultSet("SELECT count(*) FROM ask_list;");
			if(bidleft!=null){
				if(bidleft.next()){
					Mainmenu.console.append("bid_list left with " + bidleft.getString(1) + " lines.\n");
				}
			}
			if(askleft!=null){
				if(askleft.next()){
					Mainmenu.console.append("ask_list left with " + askleft.getString(1) + " lines.\n");
				}
			}

			/*
			if(rs.getLength() > 0){
				//int i = 0;
				double result;

				MomentumStrategy ms = new MomentumStrategy();
				ms.runStrategy(rs.getAllPrice());
				result = ms.evaluteTheStrategy();
				console.append("Average return of " + Double.toString(result) + "\n");
				String signal = "Buy";

				if (result > 0.0)
					signal = "Sell";
				console.append("Evaluating strategy based on: "+ signal + " Signal \n");
			} else {
				console.append("rs null");
			}*/
			askleft.close();
			bidleft.close();
			set.close();
		} catch (SQLException e) {
			System.out.println("In Mainmenu/runStrategy : " + e);
		}
	}
	
	//IMPLEMENT TRADING STRATEGY HERE
	protected void runNewStrategy() {
		try {
			ResultSet set = myDB.getResultSet("SELECT * FROM all_list;");
			MyBidList myBidList = new MyBidList();
			MyAskList myAskList = new MyAskList();
			LinkedList<ResultData> completedTrade = new LinkedList<ResultData>();
			int count = 0;
			String tmp;
			String tmpType;
			double tmpPrice;
			int tmpVol;
			long tmpID = 0;
			int buySig = 0;
			int sellSig = 0;
			double profit = 0;
			int updateLines = 0;
			int deleteLines = 0;
			Time tmpTime;

			lecMS strategy = new lecMS();

			while (set.next()){
				tmp = set.getString(5);
				tmpType = set.getString(14);
				tmpTime = set.getTime(3);
				if(tmp.equalsIgnoreCase("ENTER")){
					tmpPrice = set.getDouble(6);
					tmpVol = set.getInt(7);
					if(tmpType.equalsIgnoreCase("B")){
						tmpID = set.getLong(12);
						insertBidList(myBidList, myAskList, completedTrade,
								tmpPrice, tmpVol, tmpID, tmpTime);
					}else if(tmpType.equalsIgnoreCase("A")){
						tmpID = set.getLong(13);
						insertAskList(myBidList, myAskList, completedTrade,
								tmpPrice, tmpVol, tmpID, tmpTime);
					}

				}else if (tmp.equalsIgnoreCase("AMEND")){
					updateLines++;
					tmpPrice = set.getLong(12);
					tmpVol = set.getInt(7);
					if(tmpType.equalsIgnoreCase("B")){
						tmpID = set.getLong(12);
						myBidList.update(tmpID,tmpPrice,tmpVol,tmpTime);

					}else if(tmpType.equalsIgnoreCase("A")){
						tmpID = set.getLong(13);

					}
				}else if (tmp.equalsIgnoreCase("DELETE")){
					deleteLines++;
					if(tmpType.equalsIgnoreCase("B")){
						tmpID = set.getLong(12);
						myBidList.deleteOne(tmpID);
					}else if(tmpType.equalsIgnoreCase("A")){
						tmpID = set.getLong(13);

					}
				}
				count++;
			};
			//System.out.println("count : " + count);
			Mainmenu.console.append("Total lines read : " + count + "\n");
			Mainmenu.console.append("Total lines matched : " + completedTrade.size() + "\n");
			Mainmenu.console.append("Total lines update : " + updateLines + "\n");
			Mainmenu.console.append("Total lines delete : " + deleteLines + "\n");
			Mainmenu.console.append("bid list contains " +  myBidList.getLength() + ".\n");
			Mainmenu.console.append("ask list contains " +  myAskList.getLength() + ".\n");
			
			//update jlabels
			myStrategyResult.LinesRead.setText(Integer.toString( count));
			myStrategyResult.MatchedLines.setText(Integer.toString(completedTrade.size()));
			myStrategyResult.UpdatedLines.setText(Integer.toString(updateLines));
			myStrategyResult.DeletedLines.setText(Integer.toString(deleteLines));
			myStrategyResult.BidList.setText(Integer.toString(myAskList.getLength()));
			myStrategyResult.AskList.setText(Integer.toString(myAskList.getLength()));
			
				
				
			set.close();
		} catch (SQLException e) {
			System.out.println("In Mainmenu/runStrategy : " + e);
		}
	}
	public void insertBidList(MyBidList myBidList, MyAskList myAskList,
			LinkedList<ResultData> completedTrade, double tmpPrice, int tmpVol,
			long tmpID, Time tmpTime) {
		if(myAskList.getLength() > 0){
			long tmpAskFirstID = myAskList.get(0).getID();
			double tmpAskFirstPrice = myAskList.get(0).getPrice();
			int tmpAskFirstVol = myAskList.get(0).getVol();
			Time tmpAskFirstTime = myAskList.get(0).getTime();
			if(tmpPrice >= tmpAskFirstPrice){
				if(tmpVol > tmpAskFirstVol){
					completedTrade.add(new ResultData(tmpID,tmpAskFirstID,tmpAskFirstVol,tmpAskFirstVol,tmpTime));
					myAskList.deleteAtIndex(0);
					tmpAskFirstVol = tmpVol - tmpAskFirstVol;
					insertBidList(myBidList, myAskList,completedTrade, tmpPrice, tmpAskFirstVol ,tmpID, tmpTime);
				}else if(tmpVol == tmpAskFirstVol){
					completedTrade.add(new ResultData(tmpID,tmpAskFirstID,tmpAskFirstPrice,tmpVol,tmpTime));
					myAskList.deleteAtIndex(0);
				}else{
					completedTrade.add(new ResultData(tmpID,tmpAskFirstID,tmpAskFirstPrice,tmpVol,tmpTime));
					tmpAskFirstVol -= tmpVol;
					myAskList.updateFirst(tmpAskFirstID, tmpAskFirstPrice, tmpAskFirstVol, tmpAskFirstTime);
				}
			}else{
				myBidList.add(tmpID,tmpPrice,tmpVol,tmpTime);
			}
		}else{
			myBidList.add(tmpID,tmpPrice,tmpVol,tmpTime);
		}
	}

	public void insertAskList(MyBidList myBidList, MyAskList myAskList,
			LinkedList<ResultData> completedTrade, double tmpPrice, int tmpVol,
			long tmpID, Time tmpTime) {
		if(myBidList.getLength() > 0){
			long tmpBidFirstID = myBidList.get(0).getID();
			double tmpBidFirstPrice = myBidList.get(0).getPrice();
			int tmpBidFirstVol = myBidList.get(0).getVol();
			Time tmpBidFirstTime = myBidList.get(0).getTime();
			if(tmpPrice <= tmpBidFirstPrice){
				if(tmpVol > tmpBidFirstVol){
					completedTrade.add(new ResultData(tmpBidFirstID,tmpID,tmpPrice,tmpBidFirstVol,tmpTime));
					tmpBidFirstVol = tmpVol - tmpBidFirstVol;
					myBidList.deleteAtIndex(0);
					insertBidList(myBidList, myAskList,completedTrade, tmpPrice,tmpBidFirstVol ,tmpID, tmpTime);
				}else if(tmpVol == tmpBidFirstVol){
					completedTrade.add(new ResultData(tmpBidFirstID,tmpID,tmpPrice,tmpVol,tmpTime));
					myBidList.deleteAtIndex(0);
				}else{
					completedTrade.add(new ResultData(tmpBidFirstID,tmpID,tmpPrice,tmpVol,tmpTime));
					tmpBidFirstVol -= tmpVol;
					myBidList.updateFirst(tmpBidFirstID,tmpBidFirstPrice, tmpBidFirstVol, tmpBidFirstTime);
				}
			}else{
				myAskList.add(tmpID,tmpPrice,tmpVol,tmpTime);
			}
		}else{
			myAskList.add(tmpID,tmpPrice,tmpVol,tmpTime);
		}

	}
}
