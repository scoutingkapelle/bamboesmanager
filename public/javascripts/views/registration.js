$(document).ready(function () {

    $.fn.bootstrapSwitch.defaults.size = 'normal';
    $.fn.bootstrapSwitch.defaults.onText = 'Ja';
    $.fn.bootstrapSwitch.defaults.offText = 'Nee';

    $("[name='friday']").bootstrapSwitch();
    $("[name='saturday']").bootstrapSwitch();
    $("[name='sorting']").bootstrapSwitch();
    $("[name='selling']").bootstrapSwitch();
    $("[name='teamLeader']").bootstrapSwitch();
    $('.checkbox label').css('padding-left', 0);

    if ($('#organisation').val() == "Scouting Kapelle") {
        $('#sorting_field').addClass('hidden');
    }
});