/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

// Resets checkboxes on re-load
$('input.days').val('0').prop('checked', false);

$(document).ready(function() {

    $("#submit-preferences").on("click", function(e){
        // Creates array of days
        $('input.days').prop('checked', true);

        var val = $("input[name='doApi']:checked").val();
        if(val === 'true') {
            e.preventDefault();
            var action = $('#preferences-form').attr('action');
            var apiAction = "/v1" + action;
            $('#preferences-form').attr('action', apiAction).submit();
        }
    });


    var isMobile = window.matchMedia("only screen and (max-width: 760px)");
    if (isMobile.matches) {
        $('#preview-sched').addClass('hidden');
    }

    var $myGroup = $('#myGroup');
    $myGroup.on('show.bs.collapse','.collapse', function() {
        $myGroup.find('.collapse.in').collapse('hide');
    });

    $('#wantedProfs, #unwantedProfs, #excludedProfs').append($('#loadedProfessors').children()).trigger('chosen:update');
    $('#loadedProfessors').remove();

    // '.tbl-content' consumed little space for vertical scrollbar, scrollbar width depend on browser/os/platfrom. Here calculate the scollbar width .
    $(window).on("load resize ", function() {
      var scrollWidth = $('.tbl-content').width() - $('.tbl-content table').width();
      $('.tbl-header').css({'padding-right':scrollWidth});
    }).resize();

    // add timeblocks when button is pressed
    $('#addTimeblock').click(function() {
        var numBlock = parseInt($('#appendage').data('count')) + 1;
        $('#appendage').data('count',numBlock.toString()).attr('data-count',numBlock);
        $('#timeConflicts').clone().attr('id','timeblock-' + numBlock).appendTo('#appendage');
        var elem = "#timeblock-" + numBlock;
        $(elem).find('.btn').data('num',numBlock).attr('data-num',numBlock).parent().css('display','block');

        $(elem).find('input.days').prop('checked', false).val('0').unwrap().find('ins').remove();
        //$(elem).find('input.days').prop('checked', false);

        $(elem + " input").iCheck({
            checkboxClass: 'icheckbox_minimal',
            radioClass: 'iradio_minimal'
        });

        $('input.days:checkbox').on('ifChanged', function(event){
            $(this).attr('value',Math.abs(parseInt($(this).attr('value')) - 1));
        });

        $(elem + ' .time-only-12').datetimepicker({
            pickDate: false,
            pick12HourFormat: true,
        });

        $('.datetime-pick input:text').on('click', function(){
            $(this).closest('.datetime-pick').find('.add-on i').click();
        });
    });

    // $('#submit-preferences').on('click', function(){
    //     $('input.days').prop('checked', true);
    // });


    $('input.days:checkbox').on('ifClicked', function(event){
        $(this).attr('value',Math.abs(parseInt($(this).attr('value')) - 1));
    });

    // remove dynamic timeblock
    $('#appendage').on('click','.removeTimeblock', function() {
        var numBlock = parseInt($('#appendage').data('count')) - 1;
        $('#appendage').data('count',numBlock.toString()).attr('data-count',numBlock);
        var elem = "#timeblock-" + $(this).data('num');
        $(elem).remove();
    });

    // create and reuse modal with dynamic data
    $('#tableModal').modal({
        keyboard: true,
        backdrop: true,
        show: false
    }).on('show.bs.modal', function(e){
        $('#courseDetails').find('.temp').remove();
        var courseID = ('.' + $(e.relatedTarget).data('courseid') + ':first');
        $(courseID).clone().css("display","block").appendTo('#courseDetails');
    }).on('shown.bs.modal', function() {
        $(document).on('click', function() {
            $('#tableModal').modal('hide');
        })
    });

    // increment/decrement schedule counter
    $('#myCarousel').on('slide.bs.carousel', function(e){
        var numScheds = $('#numValid').data('count');
        if(numScheds > 0) {
            var num = parseInt($('#numSched').data('count'));
            var left = "left";
            var right = "right";
            if (e.direction == right) {
                if ((--num) < 1) {
                    num = numScheds;
                }
                if (num > numScheds) {
                    num = 1;
                }
            }
            else if (e.direction == left) {
                if ((++num) > numScheds) {
                    num = 1;
                }
                if (num < 1) {
                    num = numScheds;
                }
            }
            num = num.toString();
            $('#numSched').data('count', num);
            $('#numSched').text(num);
        }
    });

    // hover function for all equivalent courses
    $('.tableCourse').hover(function() {
        var courseClass = "." +  $(this).data('courseid') + "h";
        $(courseClass).addClass('courseHover');

    }, function() {
        var courseClass = "." + $(this).data('courseid') + "h";
        $(courseClass).removeClass('courseHover');
    });


    $.ajax({
        url: "/AnalyticsServlet",
        type: "GET",
        success: function (data) {
            var counts = data.split(",");
            $('#stats-Users').text(counts[0]);
            $('#stats-Sessions').text(counts[1]);
            $('#stats-Pageviews').text(counts[2]);
        }
    });

});