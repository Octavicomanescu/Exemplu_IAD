JQuery(function($){
	$(document).ready(function(){
		var socket=new WebSocket('ws://'+ location.host+'/flights');
		socket.onmessage=function(message){
			var flights=JSON.parse(message.data);
			var html='<table border="0" cellspacing="0" cellpadding="5">'
				+'<tr>'
				+'<th align="left"><b>Image</b></th>'
				+'<th align="left"><b>Flight</b></th>'
				+'<th align="left"><b>From</b></th>'
				+'<th align="left"><b>To</b></th>'
				+'<th align="left"><b>Airline</b></th>'
				+'<th align="left"><b>Aircraft</b></th>'
				+'<th align="right"><b>Distance</b></th>'
				+'</tr>';
			$each(flights,function(index,flight){
				html+='<tr>'
					+'<td align="left"><image src="'+flight.image+'" width="50" height="25"></td>'
					+'<td align="left">'+flight.number+'</td>'
					+'<td align="left">'+flight.from+'</td>'
					+'<td align="left">'+flight.to+'</td>'
					+'<td align="left">'+flight.airline+'</td>'
					+'<td align="left">'+flight.aircraft+'</td>'
					+'<td align="right">'+flight.distance+'</td>'
					+'</tr>'
			});
			html+='</table>';
			$('#out').html(html);
		};
	});
});