import '@testing-library/jest-dom';
import React from 'react';

// Mock lucide-react with explicit named exports
vi.mock('lucide-react', () => {
  const createIcon = (name) => {
    const Icon = React.forwardRef((props, ref) =>
      React.createElement('span', {
        ref,
        'data-testid': `icon-${name}`,
        className: props.className || '',
      })
    );
    Icon.displayName = name;
    return Icon;
  };

  return {
    Briefcase: createIcon('Briefcase'),
    User: createIcon('User'),
    Building2: createIcon('Building2'),
    Check: createIcon('Check'),
    X: createIcon('X'),
    Loader2: createIcon('Loader2'),
    Bookmark: createIcon('Bookmark'),
    MapPin: createIcon('MapPin'),
    Clock: createIcon('Clock'),
    Lock: createIcon('Lock'),
    DollarSign: createIcon('DollarSign'),
    Share2: createIcon('Share2'),
    ClipboardCheck: createIcon('ClipboardCheck'),
    CheckCircle: createIcon('CheckCircle'),
    XCircle: createIcon('XCircle'),
    Send: createIcon('Send'),
    Trash2: createIcon('Trash2'),
    Star: createIcon('Star'),
    ImagePlus: createIcon('ImagePlus'),
    CheckCircle2: createIcon('CheckCircle2'),
    Award: createIcon('Award'),
    Calendar: createIcon('Calendar'),
    AlertCircle: createIcon('AlertCircle'),
    Info: createIcon('Info'),
  };
});

// Mock window.matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
});

// Mock URL.createObjectURL
if (typeof URL.createObjectURL === 'undefined') {
  URL.createObjectURL = vi.fn(() => 'blob:mock-url');
}
if (typeof URL.revokeObjectURL === 'undefined') {
  URL.revokeObjectURL = vi.fn();
}
