$(document).ready(function () {

    $.fn.bootstrapSwitch.defaults.size = 'normal';
    $.fn.bootstrapSwitch.defaults.onText = 'Ja';
    $.fn.bootstrapSwitch.defaults.offText = 'Nee';

    $("[name='friday']").bootstrapSwitch();
    $("[name='saturday']").bootstrapSwitch();
    $("[name='sorting']").bootstrapSwitch();
    $("[name='selling']").bootstrapSwitch();
    $("[name='teamLeader']").bootstrapSwitch();
    $("[name='bbq']").bootstrapSwitch();
    $("[name='bbqPartner']").bootstrapSwitch();
    $('.checkbox label').css('padding-left', 0);

    var selling = $('#selling');
    var category = $('#category_field');

    if (selling.bootstrapSwitch('state')) category.removeClass('hidden');
    else category.addClass('hidden');

    var selected = $('#category :selected').val();
    selling.on('switchChange.bootstrapSwitch', function (event, state) {
        if (state) {
            category.removeClass('hidden');
            $('#category').val(selected)
        }
        else {
            category.addClass('hidden');
            $('#category :selected').removeAttr('selected')
        }
    });
});