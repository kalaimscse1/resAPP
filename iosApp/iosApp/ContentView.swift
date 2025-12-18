import SwiftUI
import shared

struct ContentView: View {
    @State private var showLogin = true
    
    var body: some View {
        NavigationView {
            if showLogin {
                LoginView(onLoginSuccess: {
                    showLogin = false
                })
            } else {
                MainView()
            }
        }
    }
}

struct LoginView: View {
    @State private var companyCode = ""
    @State private var username = ""
    @State private var password = ""
    @State private var isLoading = false
    @State private var errorMessage = ""
    
    let onLoginSuccess: () -> Void
    
    var body: some View {
        VStack(spacing: 20) {
            Image(systemName: "fork.knife.circle.fill")
                .resizable()
                .frame(width: 100, height: 100)
                .foregroundColor(.blue)
            
            Text("ResB")
                .font(.largeTitle)
                .fontWeight(.bold)
            
            Text("Restaurant Management System")
                .font(.subheadline)
                .foregroundColor(.gray)
            
            VStack(spacing: 16) {
                TextField("Company Code", text: $companyCode)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .autocapitalization(.none)
                
                TextField("Username", text: $username)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .autocapitalization(.none)
                
                SecureField("Password", text: $password)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
            }
            .padding(.horizontal, 32)
            
            if !errorMessage.isEmpty {
                Text(errorMessage)
                    .foregroundColor(.red)
                    .font(.caption)
            }
            
            Button(action: performLogin) {
                if isLoading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                } else {
                    Text("Login")
                        .fontWeight(.semibold)
                }
            }
            .frame(maxWidth: .infinity)
            .padding()
            .background(Color.blue)
            .foregroundColor(.white)
            .cornerRadius(10)
            .padding(.horizontal, 32)
            .disabled(isLoading)
            
            Spacer()
        }
        .padding(.top, 60)
    }
    
    func performLogin() {
        isLoading = true
        errorMessage = ""
        
        // TODO: Implement actual login using shared module
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            isLoading = false
            if !companyCode.isEmpty && !username.isEmpty && !password.isEmpty {
                onLoginSuccess()
            } else {
                errorMessage = "Please fill in all fields"
            }
        }
    }
}

struct MainView: View {
    var body: some View {
        TabView {
            DashboardView()
                .tabItem {
                    Image(systemName: "house.fill")
                    Text("Dashboard")
                }
            
            TablesView()
                .tabItem {
                    Image(systemName: "tablecells")
                    Text("Tables")
                }
            
            OrdersView()
                .tabItem {
                    Image(systemName: "list.bullet.clipboard")
                    Text("Orders")
                }
            
            MenuView()
                .tabItem {
                    Image(systemName: "menucard")
                    Text("Menu")
                }
            
            SettingsView()
                .tabItem {
                    Image(systemName: "gear")
                    Text("Settings")
                }
        }
    }
}

struct DashboardView: View {
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 16) {
                    HStack(spacing: 16) {
                        StatCard(title: "Today's Sales", value: "₹0", icon: "indianrupeesign.circle.fill", color: .green)
                        StatCard(title: "Orders", value: "0", icon: "cart.fill", color: .blue)
                    }
                    
                    HStack(spacing: 16) {
                        StatCard(title: "Active Tables", value: "0", icon: "tablecells", color: .orange)
                        StatCard(title: "Pending KOT", value: "0", icon: "clock.fill", color: .red)
                    }
                }
                .padding()
            }
            .navigationTitle("Dashboard")
        }
    }
}

struct StatCard: View {
    let title: String
    let value: String
    let icon: String
    let color: Color
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Image(systemName: icon)
                    .foregroundColor(color)
                Spacer()
            }
            Text(value)
                .font(.title)
                .fontWeight(.bold)
            Text(title)
                .font(.caption)
                .foregroundColor(.gray)
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(radius: 2)
    }
}

struct TablesView: View {
    var body: some View {
        NavigationView {
            Text("Tables View - Coming Soon")
                .navigationTitle("Tables")
        }
    }
}

struct OrdersView: View {
    var body: some View {
        NavigationView {
            Text("Orders View - Coming Soon")
                .navigationTitle("Orders")
        }
    }
}

struct MenuView: View {
    var body: some View {
        NavigationView {
            Text("Menu View - Coming Soon")
                .navigationTitle("Menu")
        }
    }
}

struct SettingsView: View {
    var body: some View {
        NavigationView {
            List {
                Section(header: Text("Account")) {
                    NavigationLink(destination: Text("Profile")) {
                        Label("Profile", systemImage: "person.circle")
                    }
                    NavigationLink(destination: Text("Restaurant Profile")) {
                        Label("Restaurant Profile", systemImage: "building.2")
                    }
                }
                
                Section(header: Text("Settings")) {
                    NavigationLink(destination: Text("General Settings")) {
                        Label("General Settings", systemImage: "gear")
                    }
                    NavigationLink(destination: Text("Printer Settings")) {
                        Label("Printer Settings", systemImage: "printer")
                    }
                }
                
                Section {
                    Button(action: {}) {
                        Label("Logout", systemImage: "rectangle.portrait.and.arrow.right")
                            .foregroundColor(.red)
                    }
                }
            }
            .navigationTitle("Settings")
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
