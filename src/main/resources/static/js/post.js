function like(bt, entityType, entityId, toUserId, postId){

			var token = $("meta[name='_csrf']").attr("content");
			var header = $("meta[name='_csrf_header']").attr("content");

			$(document).ajaxSend(function(e, xhr, option) {
				xhr.setRequestHeader(header, token);
			});

			$.post(
				"/like",
				{"entityType":entityType, "entityId":entityId, "toUserId": toUserId, "postId":postId},
				function(data){
                    console.log(data);
					data = $.parseJSON(data);
					if (data.code == 0) {
						$(bt).children("i").text(data.likeCount);
						$(bt).children("b").text(data.likeStatus==0? "赞":"已赞");
					}


				}
			);
		}