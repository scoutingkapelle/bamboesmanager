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

    const selling = $('#selling');
    const category = $('#category_field');
    const secondChoice = $('#secondChoice_field');

    if (selling.bootstrapSwitch('state')) {
        category.removeClass('hidden');
        secondChoice.removeClass('hidden');
    } else {
        category.addClass('hidden');
        secondChoice.addClass('hidden');
    }

    const selectedCategory = $('#category :selected').val();
    const selectedSecondChoice = $('#secondChoice :selected').val();
    selling.on('switchChange.bootstrapSwitch', function (event, state) {
        if (state) {
            category.removeClass('hidden');
            $('#category').val(selectedCategory)

            secondChoice.removeClass('hidden');
            $('#secondChoice').val(selectedSecondChoice)
        } else {
            category.addClass('hidden');
            $('#category option:selected').val([]);

            secondChoice.addClass('hidden');
            $('#secondChoice option:selected').val([]);
        }
    });

    $('#delete-registration').on('submit', function () {
        return confirm('Weet je het zeker?')
    });
});
