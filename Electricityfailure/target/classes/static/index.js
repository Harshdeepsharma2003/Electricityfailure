
    function switchTab(tabId) {
        // Hide all tab contents
        const tabContents = document.querySelectorAll('.tab-content');
        tabContents.forEach(content => {
            content.classList.remove('active');
        });

        // Remove active class from all tabs
        const tabs = document.querySelectorAll('.tab');
        tabs.forEach(tab => {
            tab.classList.remove('active');
        });

        // Activate the selected tab
        document.getElementById(tabId).classList.add('active');

        // Find the clicked tab and make it active
        const clickedTab = document.querySelector(`.tab[onclick="switchTab('${tabId}')"]`);
        clickedTab.classList.add('active');
    }

    document.getElementById('area-search-form').addEventListener('submit', function(e) {
        e.preventDefault();
        const pincode = document.getElementById('pincode').value;
        const zone = document.getElementById('zone').value;
        const city = document.getElementById('city').value;

        // This would typically query your backend for faults in the specific area
        alert(`Showing results for ${city}, Zone ${zone}, PIN: ${pincode}`);

        // Reset the form
        this.reset();
    });
