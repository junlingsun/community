$(function(){
	$("#sendBtn").click(send_letter);
//	$(".close").click(delete_msg);
});

function send_letter() {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    $(document).ajaxSend(function(e, xhr, option) {
    	xhr.setRequestHeader(header, token);
    });

    var toUsername = $("#recipient-name").val();
    var content = $("#message-text").val();
    $("#sendModal").modal("hide");

    $.post(
        "/message/publish",
    	{"toUsername": toUsername, "content": content},
    	function(data){
    		data = $.parseJSON(data);


    		$("#hintBody").text(data.msg);
    		$("#hintModal").modal("show");

    		setTimeout(function(){
                $("#hintModal").modal("hide");
                window.location.reload();
            }, 2000);
    	}

    );

}

//
//function delete_msg() {
//	// TODO 删除数据
//	$(this).parents(".media").remove();
//}