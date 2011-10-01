package org.openmeetings.app.persistence.beans.poll;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.openmeetings.app.persistence.beans.recording.RoomClient;

@Entity
@Table(name = "room_poll_answers")
public class RoomPollAnswers {
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "roomclient_id")
	private RoomClient votedClient;
	@Column(name = "answer")
	private Boolean answer;
	@Column(name = "pointList")
	private Integer pointList;
	@Column(name = "voteDate")
	private Date voteDate;
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "room_poll_id")
	private RoomPoll roomPoll;

	/**
	 * @return the answer
	 */
	public Boolean getAnswer() {
		return answer;
	}

	/**
	 * @param answer
	 *            the answer to set
	 */
	public void setAnswer(Boolean answer) {
		this.answer = answer;
	}

	/**
	 * @return the pointList
	 */
	public Integer getPointList() {
		return pointList;
	}

	/**
	 * @param pointList
	 *            the pointList to set
	 */
	public void setPointList(Integer pointList) {
		this.pointList = pointList;
	}

	/**
	 * @return the voteDate
	 */
	public Date getVoteDate() {
		return voteDate;
	}

	/**
	 * @param voteDate
	 *            the voteDate to set
	 */
	public void setVoteDate(Date voteDate) {
		this.voteDate = voteDate;
	}

	/**
	 * @return the votedClient
	 */
	public RoomClient getVotedClient() {
		return votedClient;
	}

	/**
	 * @param votedClient
	 *            the votedClient to set
	 */
	public void setVotedClient(RoomClient votedClient) {
		this.votedClient = votedClient;
	}

	public RoomPoll getRoomPoll() {
		return roomPoll;
	}

	public void setRoomPoll(RoomPoll roomPoll) {
		this.roomPoll = roomPoll;
	}

}
