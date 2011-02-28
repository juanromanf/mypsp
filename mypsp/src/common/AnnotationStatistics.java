package common;

import java.util.ArrayList;
import java.util.List;

import annotation.Loc;
import annotation.LogInt;
import annotation.LogT;
import annotation.Plan;

public class AnnotationStatistics {

	private List<LogT> logTList;
	private List<LogInt> logIntList;
	private List<Loc> locList;
	private Plan plan;

	public AnnotationStatistics() {

		this.logTList = new ArrayList<LogT>();
		this.logIntList = new ArrayList<LogInt>();
		this.locList = new ArrayList<Loc>();
	}

	public void addLogT(LogT a) {
		this.logTList.add(a);
	}

	public void addLogInt(LogInt a) {
		this.logIntList.add(a);
	}

	public void addLoc(Loc a) {
		this.locList.add(a);
	}

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public List<LogT> getLogTList() {
		return logTList;
	}

	public void setLogTList(List<LogT> logTList) {
		this.logTList = logTList;
	}

	public List<LogInt> getLogIntList() {
		return logIntList;
	}

	public void setLogIntList(List<LogInt> logIntList) {
		this.logIntList = logIntList;
	}

	public List<Loc> getLocList() {
		return locList;
	}

	public void setLocList(List<Loc> locList) {
		this.locList = locList;
	}

	public double getLogTSum() {

		double sum = 0;
		for (LogT a : this.logTList) {
			sum += a.time();
		}
		return sum;
	}

	public double getLogIntSum() {

		double sum = 0;
		for (LogInt a : this.logIntList) {
			sum += a.time();
		}
		return sum;
	}

	public double getLocSum() {

		double sum = 0;
		for (Loc a : this.locList) {
			sum += a.size();
		}
		return sum;
	}

	public double getRealTime() {
		return getLogTSum() - getLogIntSum();
	}
	
	public int getPlannedTime() {
		return plan.time();
	}

}
