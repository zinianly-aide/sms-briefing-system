import '@testing-library/jest-dom';

// Mock window.matchMedia for Ant Design responsive hooks
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation((query) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn()
  }))
});

// Mock react-router-dom
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => vi.fn(),
    useParams: () => ({}),
    useLocation: () => ({ pathname: '/' }),
    BrowserRouter: ({ children }) => children,
    Routes: ({ children }) => children,
    Route: ({ children }) => children,
    Navigate: () => null,
  };
});

// Mock AuthContext
vi.mock('../context/AuthContext', () => ({
  useAuth: () => ({
    user: { username: 'testuser', displayName: '测试用户', role: 'admin' },
    authenticated: true,
    displayName: '测试用户',
    login: vi.fn(),
    logout: vi.fn(),
  }),
  AuthProvider: ({ children }) => children,
}));
