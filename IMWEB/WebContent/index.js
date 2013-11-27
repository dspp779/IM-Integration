$(document).ready(function(){
	getFriendList();
});

function getFriendList() {
	$.ajax({
		type: 'GET',
		url: encodeURI('im'),
		headers: {'Accept':'application/json'},
		success: function(data, textStatus, jqXHR) {
			var friends = eval(data);
			htmlFrag = '';
			for ( var i = 0; i < friends.length && i < 10; i++) {
				htmlFrag += '<div class="buddyBlock"><h3>' + friends[i]
						+ '</h3></div>';
			}
			$('div#fromlist').html(htmlFrag).children('div.buddyBlock').click(function(){
				var item = $(this).siblings('div.selected');
				$(this).toggleClass('selected');
				item.removeClass('selected');
			});
			$('div#tolist').html(htmlFrag).children('div.buddyBlock').click(function(){
				$(this).toggleClass('selected');
			});
		},
		error: function(jqXHR, textStatus, errorThrown) { // error callback
			if(errorThrown === 'Not Found') {
				
			}
		}
	});
}

var newIntegration = function() {
	$('div#info-area').hide();
	from = $('div#fromlist > div.selected:first > h3').text();
	if(from == '')
		return;
	to = '';
	$('div#tolist > div.selected > h3').each(function( index ) {
		to += '"' + $(this).text() + '",';
	});
	to = '[' + to.substring(0, to.length - 1) + ']';
	$.ajax({
		type: 'POST',
		url: encodeURI('im?from=' + from),
		headers: {'Content-Type': 'application/json;charset=UTF-8'},
		data: to,
		success: function(data, textStatus, jqXHR) {
			if(to!='[]')
				$('div#info-area').html(from + ' => ' + to + ' success!').show();
			else
				$('div#info-area').html('clear integration from ' + from + ' success!').show();
		},
		error: function(jqXHR, textStatus, errorThrown) {  // error callback
			$('div#info-area').html('server no response!').show();
		}
	});
};

var resetIntegration = function() {
	$('div#tolist > div.selected').removeClass('selected');
	newIntegration();
};