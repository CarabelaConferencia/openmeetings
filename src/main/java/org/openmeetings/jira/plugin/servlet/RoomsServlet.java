package org.openmeetings.jira.plugin.servlet;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmeetings.jira.plugin.ao.omrooms.Room;
import org.openmeetings.jira.plugin.ao.omrooms.RoomService;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
 
import static com.google.common.base.Preconditions.*;
 
public final class RoomsServlet extends HttpServlet
{
    private final RoomService roomService;
    private TemplateRenderer templateRenderer;
    
    private static final String LIST_BROWSER_TEMPLATE = "/templates/omrooms/list.vm";
    private static final String NEW_BROWSER_TEMPLATE = "/templates/omrooms/new.vm";
    private static final String EDIT_BROWSER_TEMPLATE = "/templates/omrooms/edit.vm";
 
    public RoomsServlet(RoomService roomService, TemplateRenderer templateRenderer)
    {
        this.roomService = checkNotNull(roomService);
        this.templateRenderer = templateRenderer;
    }
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
    	//User user = getCurrentUser(req);
        
        if ("y".equals(req.getParameter("new"))) {
        	// Renders new.vm template if the "new" parameter is passed
            
            // Create an empty context map to pass into the render method
            Map<String, Object> context = Maps.newHashMap();
            // Make sure to set the contentType otherwise bad things happen
            res.setContentType("text/html;charset=utf-8");
            // Render the velocity template (new.vm). Since the new.vm template 
            // doesn't need to render any in dynamic content, we just pass it an empty context
           templateRenderer.render(NEW_BROWSER_TEMPLATE, context, res.getWriter());
        	
        }else if("y".equals(req.getParameter("edit"))){
        	// Renders edit.vm template if the "edit" parameter is passed
            
            // Retrieve issue with the specified key
//            IssueService.IssueResult issue = issueService.getIssue(getCurrentUser(req), 
//                                                                   req.getParameter("key"));
        	Integer id = Integer.valueOf(req.getParameter("id"));
//        	Boolean isAllowedRecording = Boolean.valueOf(req.getParameter("isAllowedRecording"));
//        	Boolean isAudioOnly = Boolean.valueOf(req.getParameter("isAudioOnly"));
//        	Boolean isModeratedRoom = Boolean.valueOf(req.getParameter("isModeratedRoom"));
//        	String name = req.getParameter("name");
//        	Long numberOfParticipent = Long.valueOf(req.getParameter("numberOfParticipent"));  
//        	Long roomType = Long.valueOf(req.getParameter("roomType")); 
        	
        	        	
//        	Room room =  roomService.update(id, isAllowedRecording, isAudioOnly, isModeratedRoom, name, numberOfParticipent, roomType);
        	
        	Room room = roomService.getRoom(id);
        	
            Map<String, Object> context = Maps.newHashMap();
            context.put("issue", room);
            res.setContentType("text/html;charset=utf-8");
            // Render the template with the issue inside the context
            templateRenderer.render(EDIT_BROWSER_TEMPLATE, context, res.getWriter());
        	
        }else if("y".equals(req.getParameter("delete"))){
        	
        	
        }else {
            // Render the list of issues (list.vm) if no params are passed in
            List<Room> rooms =  roomService.all();
            Map<String, Object> context = Maps.newHashMap();
            context.put("rooms", rooms);
            res.setContentType("text/html;charset=utf-8");
            // Pass in the list of rooms as the context
            templateRenderer.render(LIST_BROWSER_TEMPLATE, context, res.getWriter());
        }
    	
    	
        
    	final PrintWriter w = res.getWriter();
        w.write("<h1>Rooms</h1>");
 
        // the form to post more TODOs
        w.write("<form method=\"post\">");
        w.write("<input type=\"text\" name=\"task\" size=\"25\"/>");
        w.write("&nbsp;&nbsp;");
        w.write("<input type=\"submit\" name=\"submit\" value=\"Add\"/>");
        w.write("</form>");
 
        w.write("<ol>");
 
        for (Room room : roomService.all()) 
        {
            w.printf("<li><%2$s> %s </%2$s></li>", room.getName(), room.getIsModeratedRoom() ? "strike" : "strong");
        }
 
        w.write("</ol>");
        w.write("<script language='javascript'>document.forms[0].elements[0].focus();</script>");
 
        w.close();
    }
 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
    	if ("y".equals(req.getParameter("edit"))) {

    		
    	}else{
    		
        	Boolean isAllowedRecording = Boolean.valueOf(req.getParameter("isAllowedRecording"));
        	Boolean isAudioOnly = Boolean.valueOf(req.getParameter("isAudioOnly"));
        	Boolean isModeratedRoom = Boolean.valueOf(req.getParameter("isModeratedRoom"));
        	String name = req.getParameter("roomname");
        	Long numberOfParticipent = Long.valueOf(req.getParameter("numberOfParticipent"));  
        	Long roomType = Long.valueOf(req.getParameter("roomType")); 
        	
        	roomService.add(isAllowedRecording, isAudioOnly, isModeratedRoom, name, numberOfParticipent, roomType);
            //roomService.add(description, true, true, true, "name", 4L, 1L);

            res.sendRedirect(req.getContextPath() + "/plugins/servlet/openmeetingsrooms");
    		
    	}
        final String description = req.getParameter("task");
//        final String isAllowedRecording = req.getParameter("isAllowedRecording");
//        final String isAudioOnly = req.getParameter("isAudioOnly");
//        final String isModeratedRoom = req.getParameter("isModeratedRoom");
//        final String name = req.getParameter("name");
//        final Long numberOfParticipent = Long.valueOf(req.getParameter("numberOfParticipent"));
//        final Long roomType = Long.valueOf(req.getParameter("roomType"));
        
        //roomService.add(isAllowedRecording, isAudioOnly, isModeratedRoom, name, numberOfParticipent, roomType);
        //roomService.add(true, true, true, "name", 4L, 1L);

        //res.sendRedirect(req.getContextPath() + "/plugins/servlet/openmeetingsrooms");
    }
}