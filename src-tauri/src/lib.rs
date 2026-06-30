use std::fs;
use std::path::PathBuf;
use tauri::Manager;
use tauri::tray::TrayIconBuilder;
use tauri_plugin_global_shortcut::GlobalShortcutExt;
use tauri_plugin_global_shortcut::{Code, Modifiers, Shortcut, ShortcutState};

fn get_data_path(app: &tauri::AppHandle) -> PathBuf {
    let app_dir = app
        .path()
        .app_data_dir()
        .expect("failed to get app data dir");
    fs::create_dir_all(&app_dir).ok();
    app_dir.join("data.json")
}

#[tauri::command]
fn load_data(app: tauri::AppHandle) -> Result<String, String> {
    let path = get_data_path(&app);
    fs::read_to_string(&path).map_err(|e| format!("failed to read data: {}", e))
}

#[tauri::command]
fn save_data(app: tauri::AppHandle, data: String) -> Result<(), String> {
    let path = get_data_path(&app);
    fs::write(&path, &data).map_err(|e| format!("failed to save data: {}", e))
}

fn toggle_window(app: &tauri::AppHandle) {
    if let Some(window) = app.get_webview_window("main") {
        if window.is_visible().unwrap_or(false) {
            window.hide().ok();
        } else {
            #[cfg(target_os = "macos")]
            {
                if let Ok(Some(monitor)) = window.current_monitor() {
                    let size = monitor.size();
                    let window_size = window.outer_size().unwrap_or(tauri::PhysicalSize::new(360, 480));
                    let x = size.width as i32 - window_size.width as i32 - 20;
                    let y = 30;
                    window.set_position(tauri::Position::Physical(tauri::PhysicalPosition::new(x, y))).ok();
                }
            }
            window.show().ok();
            window.set_focus().ok();
        }
    }
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_shell::init())
        .plugin(
            tauri_plugin_global_shortcut::Builder::new()
                .with_handler(|app, shortcut, event| {
                    if event.state == ShortcutState::Pressed
                        && shortcut.matches(Modifiers::META | Modifiers::SHIFT, Code::KeyT)
                    {
                        toggle_window(app);
                    }
                })
                .build(),
        )
        .setup(|app| {
            let _tray = TrayIconBuilder::new()
                .icon(app.default_window_icon().unwrap().clone())
                .tooltip("LiteToDo")
                .on_tray_icon_event(|tray, event| {
                    if let tauri::tray::TrayIconEvent::Click { .. } = event {
                        toggle_window(tray.app_handle());
                    }
                })
                .build(app)?;

            app.global_shortcut()
                .register(Shortcut::new(Some(Modifiers::META | Modifiers::SHIFT), Code::KeyT))?;

            if let Some(window) = app.get_webview_window("main") {
                window.hide().ok();
            }

            Ok(())
        })
        .on_window_event(|window, event| {
            if let tauri::WindowEvent::Focused(false) = event {
                window.hide().ok();
            }
        })
        .invoke_handler(tauri::generate_handler![load_data, save_data])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
