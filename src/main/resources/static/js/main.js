/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

// Resets checkboxes on re-load
// $('input.days').val('0').prop('checked', false);

$(document).ready(function() {

    $("#submit-preferences").on("click", function(e){

        var daysArray = [];
        $('input.days').each(function () {
            if($(this).is(":checked")) {
                daysArray.push($(this).val());
            } else {
                daysArray.push('0'); // acts as a buffer, zeros removed below
            }
        })

        var indexStart = 0;
        var daysPerWeekBuffer = 5;

        $('input.timeblockDays').each(function () {
            $(this).val(daysArray.slice(indexStart, indexStart + daysPerWeekBuffer).filter(function(elem){
                return elem !== '0';
            }).toString());
            indexStart += daysPerWeekBuffer;
        });

        var val = $("input[name='api_key']:checked").val();
        var beta_key = '8ad0544d-62d2-4937-8d5a-2f5dae04209e'; // temporary
        if(val === beta_key) {
            e.preventDefault();
            $('#isMobileBrowser').remove();
            var action = $('#preferences-form').attr('action');
            var apiAction = "/v1" + action;
            $('#preferences-form').attr('action', apiAction).submit();
        }
    });


    $('#bug-button').on('click', function () {
        $(this).prop("disabled", true).html('Sending..').addClass("loading");
        $.ajax({
            type: 'POST',
            url: '/ajax/submit-bug',
            data: {
                userInput: $("#bug-text").val()
            },
            success: function () {
                $('#bug-button').html('Thanks!').addClass("disabled").prop("disabled", true);
            }
        });
    });

    $('#suggestion-button').on('click', function () {
        $(this).prop("disabled", true).html('Sending..').addClass("loading");
        $.ajax({
            type: 'POST',
            url: '/ajax/submit-suggestion',
            data: {
                userInput: $("#suggestion-text").val()
            },
            success: function () {
                $('#suggestion-button').html('Thanks!').addClass("disabled").prop("disabled", true);
            }
        });
    });

    var isMobile = window.matchMedia("only screen and (max-width: 760px)");
    if (isMobile.matches) {
        $('#preview-sched').addClass('hidden');
    }

    var $myGroup = $('#myGroup');
    $myGroup.on('show.bs.collapse','.collapse', function() {
        $myGroup.find('.collapse.in').collapse('hide');
    });

    $('#favoredProfs, #disfavoredProfs, #excludedProfs').append($('#loadedProfessors').children()).trigger('chosen:update');
    $('#loadedProfessors').remove();

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
        $(elem).find('input.days').prop('checked', false).unwrap().find('ins').remove();

        $(elem + " input").iCheck({
            checkboxClass: 'icheckbox_minimal',
            radioClass: 'iradio_minimal'
        });

        $(elem + ' .time-only-12').datetimepicker({
            pickDate: false,
            pick12HourFormat: true,
        });

        $('.datetime-pick input:text').on('click', function(){
            $(this).closest('.datetime-pick').find('.add-on i').click();
        });
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