$(document).ready(function () {

    $.fn.bootstrapSwitch.defaults.size = 'normal';
    $.fn.bootstrapSwitch.defaults.onText = 'Ja';
    $.fn.bootstrapSwitch.defaults.offText = 'Nee';

    const friday = $('#friday');
    const saturday = $('#saturday');
    const sorting = $('#sorting');
    const selling = $('#selling');

    friday.bootstrapSwitch();
    saturday.bootstrapSwitch();
    sorting.bootstrapSwitch();
    selling.bootstrapSwitch();
    $('.checkbox label').css('padding-left', 0);

    const categoryField = $('#category_field');

    if (selling.bootstrapSwitch('state')) categoryField.removeClass('hidden');
    else categoryField.addClass('hidden');

    const selected = $('#category :selected').val();
    selling.on('switchChange.bootstrapSwitch', function (event, state) {
        if (state) {
            categoryField.removeClass('hidden');
            $('#category').val(selected)
        } else {
            categoryField.addClass('hidden');
            $('#category option:selected').val([]);
        }
    });

    const organisations = $('#organisation');
    const selected_organisation = organisations.find(':selected');
    updateSorting(selected_organisation);
    if (selected_organisation.val().length === 0) {
        $('#group option').each(function (i, option) {
            $(option).addClass('hidden')
        });
    } else {
        $('#group option').each(function (i, option) {
            if ($(option).val().length === 0) {
                $(option).text("Selecteer een groep").removeClass('hidden')
            } else if ($(option).val().split('#')[0] === selected_organisation.val()) {
                $(option).removeClass('hidden')
            } else {
                $(option).addClass('hidden')
            }
        })
    }

    organisations.change(function () {
        const organisation = $('#organisation').find(':selected');
        updateGroups(organisation.val());
        updateSorting(organisation)
    });

    function updateGroups(organisation) {
        if (organisation.length === 0) {
            $('#group option').each(function (i, option) {
                if ($(option).val().length === 0) {
                    $(option).text("Selecteer eerst een vereniging").prop('selected', true)
                } else {
                    $(option).addClass('hidden')
                }
            })
        } else {
            $('#group option').each(function (i, option) {
                if ($(option).val().length === 0) {
                    $(option).text("Selecteer een groep").prop('selected', true).removeClass('hidden')
                } else if ($(option).val().split('#')[0] === organisation) {
                    $(option).removeClass('hidden')
                } else {
                    $(option).addClass('hidden')
                }
            })
        }
    }

    function updateSorting(organisation) {
        if (organisation.text() === "Scouting Kapelle" || organisation.val().length === 0) {
            $('#sorting_wrapper').addClass('hidden');
            sorting.prop('checked', false)
        } else {
            $('#sorting_wrapper').removeClass('hidden')
        }
    }
});
